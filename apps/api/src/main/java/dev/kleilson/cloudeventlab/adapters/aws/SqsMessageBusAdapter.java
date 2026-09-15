package dev.kleilson.cloudeventlab.adapters.aws;

import dev.kleilson.cloudeventlab.application.port.MessageBusPort;
import jakarta.annotation.PreDestroy;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.QueueDoesNotExistException;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

/**
 * SQS messaging against LocalStack (LOCAL EMULATOR). Failed handlers leave the message (visibility
 * timeout retry) — full DLQ redrive is APPROX / optional; see docs/aws/README.md.
 */
@Component
@Profile("aws")
@ConditionalOnProperty(name = "lab.cloud.provider", havingValue = "aws")
public class SqsMessageBusAdapter implements MessageBusPort {

  private static final Logger log = LoggerFactory.getLogger(SqsMessageBusAdapter.class);

  private final SqsClient sqs;
  private final Map<String, String> queueUrls = new ConcurrentHashMap<>();
  private final Map<String, Consumer<String>> handlers = new ConcurrentHashMap<>();
  private final ExecutorService poller = Executors.newCachedThreadPool();
  private final AtomicBoolean running = new AtomicBoolean(true);
  private final int waitSeconds;
  private final int visibilitySeconds;

  public SqsMessageBusAdapter(
      @Value("${lab.aws.endpoint}") String endpoint,
      @Value("${lab.aws.region:us-east-1}") String region,
      @Value("${lab.aws.access-key-id:test}") String accessKeyId,
      @Value("${lab.aws.secret-access-key:test}") String secretAccessKey,
      @Value("${lab.aws.use-static-credentials:true}") boolean useStaticCredentials,
      @Value("${lab.aws.sqs.wait-seconds:5}") int waitSeconds,
      @Value("${lab.aws.sqs.visibility-seconds:30}") int visibilitySeconds) {
    this.waitSeconds = waitSeconds;
    this.visibilitySeconds = visibilitySeconds;
    var builder =
        SqsClient.builder().endpointOverride(URI.create(endpoint)).region(Region.of(region));
    if (useStaticCredentials) {
      builder.credentialsProvider(
          StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)));
    } else {
      builder.credentialsProvider(DefaultCredentialsProvider.builder().build());
    }
    this.sqs = builder.build();
  }

  /** Test / manual wiring without Spring. */
  public SqsMessageBusAdapter(SqsClient sqs, int waitSeconds, int visibilitySeconds) {
    this.sqs = sqs;
    this.waitSeconds = waitSeconds;
    this.visibilitySeconds = visibilitySeconds;
  }

  @Override
  public void publish(String topicOrQueue, String messageBody) {
    String url = queueUrl(topicOrQueue);
    sqs.sendMessage(SendMessageRequest.builder().queueUrl(url).messageBody(messageBody).build());
  }

  @Override
  public void subscribe(String topicOrQueue, Consumer<String> handler) {
    handlers.put(topicOrQueue, handler);
    queueUrl(topicOrQueue);
    poller.submit(() -> pollLoop(topicOrQueue));
  }

  private void pollLoop(String queue) {
    while (running.get() && !Thread.currentThread().isInterrupted()) {
      Consumer<String> handler = handlers.get(queue);
      if (handler == null) {
        break;
      }
      try {
        var response =
            sqs.receiveMessage(
                ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl(queue))
                    .maxNumberOfMessages(5)
                    .waitTimeSeconds(waitSeconds)
                    .visibilityTimeout(visibilitySeconds)
                    .build());
        List<?> messages = response.messages();
        if (messages == null || messages.isEmpty()) {
          continue;
        }
        response
            .messages()
            .forEach(
                msg -> {
                  try {
                    handler.accept(msg.body());
                    sqs.deleteMessage(
                        DeleteMessageRequest.builder()
                            .queueUrl(queueUrl(queue))
                            .receiptHandle(msg.receiptHandle())
                            .build());
                  } catch (Exception ex) {
                    log.warn("SQS handler failed; message left for retry queue={}", queue, ex);
                  }
                });
      } catch (Exception ex) {
        if (running.get()) {
          log.warn("SQS poll error queue={}", queue, ex);
          sleepQuietly(1000);
        }
      }
    }
  }

  private String queueUrl(String name) {
    return queueUrls.computeIfAbsent(
        name,
        q -> {
          try {
            return sqs.getQueueUrl(GetQueueUrlRequest.builder().queueName(q).build()).queueUrl();
          } catch (QueueDoesNotExistException e) {
            sqs.createQueue(CreateQueueRequest.builder().queueName(q).build());
            return sqs.getQueueUrl(GetQueueUrlRequest.builder().queueName(q).build()).queueUrl();
          }
        });
  }

  private static void sleepQuietly(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @PreDestroy
  public void shutdown() {
    running.set(false);
    poller.shutdownNow();
    try {
      poller.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    sqs.close();
  }
}
