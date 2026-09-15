package dev.kleilson.cloudeventlab.adapters.azure;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueServiceClientBuilder;
import com.azure.storage.queue.models.QueueMessageItem;
import com.azure.storage.queue.models.QueueStorageException;
import dev.kleilson.cloudeventlab.application.port.MessageBusPort;
import jakarta.annotation.PreDestroy;
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
 * Azure Queue messaging against Azurite (LOCAL EMULATOR). No Service Bus–style DLQ here — failed
 * handlers leave the message visible again after visibility timeout (APPROX). See docs/azure.
 */
@Component
@Profile("azure")
@ConditionalOnProperty(name = "lab.cloud.provider", havingValue = "azure")
public class AzureQueueMessageBusAdapter implements MessageBusPort {

  private static final Logger log = LoggerFactory.getLogger(AzureQueueMessageBusAdapter.class);

  private final String connectionString;
  private final Map<String, QueueClient> queues = new ConcurrentHashMap<>();
  private final Map<String, Consumer<String>> handlers = new ConcurrentHashMap<>();
  private final ExecutorService poller = Executors.newCachedThreadPool();
  private final AtomicBoolean running = new AtomicBoolean(true);

  public AzureQueueMessageBusAdapter(
      @Value("${lab.azure.storage-connection}") String connectionString,
      @Value("${lab.azure.queue.endpoint:http://127.0.0.1:10001/devstoreaccount1}")
          String queueEndpoint) {
    this.connectionString =
        AzureBlobObjectStorageAdapter.resolveQueueConnection(connectionString, queueEndpoint);
  }

  /** Test / manual wiring without Spring. */
  public AzureQueueMessageBusAdapter(String connectionString) {
    this.connectionString = connectionString;
  }

  @Override
  public void publish(String topicOrQueue, String messageBody) {
    queue(topicOrQueue).sendMessage(messageBody);
  }

  @Override
  public void subscribe(String topicOrQueue, Consumer<String> handler) {
    handlers.put(topicOrQueue, handler);
    queue(topicOrQueue);
    poller.submit(() -> pollLoop(topicOrQueue));
  }

  private void pollLoop(String name) {
    QueueClient client = queue(name);
    while (running.get() && !Thread.currentThread().isInterrupted()) {
      Consumer<String> handler = handlers.get(name);
      if (handler == null) {
        break;
      }
      try {
        QueueMessageItem msg = client.receiveMessage();
        if (msg == null) {
          sleepQuietly(500);
          continue;
        }
        try {
          handler.accept(msg.getBody().toString());
          client.deleteMessage(msg.getMessageId(), msg.getPopReceipt());
        } catch (Exception ex) {
          log.warn("Azure Queue handler failed; message left for retry queue={}", name, ex);
          // Visibility timeout from the service will make it reappear (APPROX retry).
        }
      } catch (QueueStorageException ex) {
        if (running.get()) {
          log.warn("Azure Queue poll error queue={}", name, ex);
          sleepQuietly(1000);
        }
      } catch (Exception ex) {
        if (running.get()) {
          log.warn("Azure Queue poll error queue={}", name, ex);
          sleepQuietly(1000);
        }
      }
    }
  }

  private QueueClient queue(String name) {
    return queues.computeIfAbsent(
        name,
        n -> {
          QueueClient client =
              new QueueServiceClientBuilder()
                  .connectionString(connectionString)
                  .buildClient()
                  .getQueueClient(n);
          client.createIfNotExists();
          return client;
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
  }
}
