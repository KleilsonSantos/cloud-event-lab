package dev.kleilson.cloudeventlab.adapters.gcp;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.AcknowledgeRequest;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;
import dev.kleilson.cloudeventlab.application.port.MessageBusPort;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
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

/**
 * Pub/Sub messaging against the gcloud emulator (LOCAL EMULATOR). Dead-letter topics are APPROX on
 * the emulator — failed handlers skip ack so the message becomes redeliverable. See docs/gcp.
 */
@Component
@Profile("gcp")
@ConditionalOnProperty(name = "lab.cloud.provider", havingValue = "gcp")
public class PubSubMessageBusAdapter implements MessageBusPort {

  private static final Logger log = LoggerFactory.getLogger(PubSubMessageBusAdapter.class);

  private final String projectId;
  private final ManagedChannel channel;
  private final TransportChannelProvider channelProvider;
  private final CredentialsProvider credentialsProvider = NoCredentialsProvider.create();
  private final TopicAdminClient topicAdmin;
  private final SubscriptionAdminClient subscriptionAdmin;
  private final Map<String, Publisher> publishers = new ConcurrentHashMap<>();
  private final Map<String, Consumer<String>> handlers = new ConcurrentHashMap<>();
  private final ExecutorService poller = Executors.newCachedThreadPool();
  private final AtomicBoolean running = new AtomicBoolean(true);

  public PubSubMessageBusAdapter(
      @Value("${lab.gcp.project-id}") String projectId,
      @Value("${lab.gcp.pubsub-emulator-host}") String emulatorHost)
      throws IOException {
    this.projectId = projectId;
    String host =
        emulatorHost.replace("http://", "").replace("https://", "");
    this.channel = ManagedChannelBuilder.forTarget(host).usePlaintext().build();
    this.channelProvider =
        FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
    this.topicAdmin =
        TopicAdminClient.create(
            TopicAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build());
    this.subscriptionAdmin =
        SubscriptionAdminClient.create(
            SubscriptionAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build());
  }

  @Override
  public void publish(String topicOrQueue, String messageBody) {
    try {
      ensureTopicAndSubscription(topicOrQueue);
      Publisher publisher =
          publishers.computeIfAbsent(
              topicOrQueue,
              name -> {
                try {
                  return Publisher.newBuilder(ProjectTopicName.of(projectId, name))
                      .setChannelProvider(channelProvider)
                      .setCredentialsProvider(credentialsProvider)
                      .build();
                } catch (IOException e) {
                  throw new IllegalStateException("Failed to create Pub/Sub publisher", e);
                }
              });
      publisher
          .publish(
              PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(messageBody)).build())
          .get(10, TimeUnit.SECONDS);
    } catch (Exception e) {
      throw new IllegalStateException("Pub/Sub publish failed: " + topicOrQueue, e);
    }
  }

  @Override
  public void subscribe(String topicOrQueue, Consumer<String> handler) {
    handlers.put(topicOrQueue, handler);
    ensureTopicAndSubscription(topicOrQueue);
    poller.submit(() -> pullLoop(topicOrQueue));
  }

  private void pullLoop(String topic) {
    ProjectSubscriptionName subName = ProjectSubscriptionName.of(projectId, subscriptionId(topic));
    try (GrpcSubscriberStub subscriber =
        GrpcSubscriberStub.create(
            SubscriberStubSettings.newBuilder()
                .setTransportChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build())) {
      while (running.get() && !Thread.currentThread().isInterrupted()) {
        Consumer<String> handler = handlers.get(topic);
        if (handler == null) {
          break;
        }
        try {
          var response =
              subscriber
                  .pullCallable()
                  .call(
                      PullRequest.newBuilder()
                          .setSubscription(subName.toString())
                          .setMaxMessages(5)
                          .build());
          if (response.getReceivedMessagesList().isEmpty()) {
            sleepQuietly(500);
            continue;
          }
          for (var received : response.getReceivedMessagesList()) {
            try {
              handler.accept(received.getMessage().getData().toStringUtf8());
              subscriber
                  .acknowledgeCallable()
                  .call(
                      AcknowledgeRequest.newBuilder()
                          .setSubscription(subName.toString())
                          .addAckIds(received.getAckId())
                          .build());
            } catch (Exception ex) {
              log.warn(
                  "Pub/Sub handler failed; ack skipped (retry via ack deadline) topic={}",
                  topic,
                  ex);
            }
          }
        } catch (Exception ex) {
          if (running.get()) {
            log.warn("Pub/Sub pull error topic={}", topic, ex);
            sleepQuietly(1000);
          }
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("Pub/Sub subscriber stub failed", e);
    }
  }

  private void ensureTopicAndSubscription(String topic) {
    ProjectTopicName topicName = ProjectTopicName.of(projectId, topic);
    try {
      topicAdmin.getTopic(topicName.toString());
    } catch (Exception e) {
      try {
        topicAdmin.createTopic(Topic.newBuilder().setName(topicName.toString()).build());
      } catch (Exception ignored) {
        // concurrent create
      }
    }
    ProjectSubscriptionName subName = ProjectSubscriptionName.of(projectId, subscriptionId(topic));
    try {
      subscriptionAdmin.getSubscription(subName.toString());
    } catch (Exception e) {
      try {
        subscriptionAdmin.createSubscription(
            Subscription.newBuilder()
                .setName(subName.toString())
                .setTopic(topicName.toString())
                .setPushConfig(PushConfig.getDefaultInstance())
                .setAckDeadlineSeconds(20)
                .build());
      } catch (Exception ignored) {
        // concurrent create
      }
    }
  }

  private static String subscriptionId(String topic) {
    return topic + "-sub";
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
    publishers
        .values()
        .forEach(
            p -> {
              try {
                p.shutdown();
                p.awaitTermination(5, TimeUnit.SECONDS);
              } catch (Exception ignored) {
                // best-effort
              }
            });
    topicAdmin.close();
    subscriptionAdmin.close();
    channel.shutdownNow();
  }
}
