package dev.kleilson.cloudeventlab.adapters.local;

import dev.kleilson.cloudeventlab.application.port.MessageBusPort;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * In-process message bus for local/test. OPEN SOURCE ALTERNATIVE to cloud queues — not a cloud
 * emulator. Disabled when {@code lab.cloud.provider} is aws/azure/gcp.
 */
@Component
@ConditionalOnExpression(
    "!'${lab.cloud.provider:local}'.equals('aws') and !'${lab.cloud.provider:local}'.equals('azure') and !'${lab.cloud.provider:local}'.equals('gcp')")
public class InMemoryMessageBusAdapter implements MessageBusPort {

  private final Map<String, Consumer<String>> subscribers = new ConcurrentHashMap<>();
  private final ExecutorService executor = Executors.newCachedThreadPool();

  @Override
  public void publish(String topicOrQueue, String messageBody) {
    Consumer<String> handler = subscribers.get(topicOrQueue);
    if (handler != null) {
      executor.submit(() -> handler.accept(messageBody));
    }
  }

  @Override
  public void subscribe(String topicOrQueue, Consumer<String> handler) {
    subscribers.put(topicOrQueue, handler);
  }

  @PreDestroy
  void shutdown() {
    executor.shutdownNow();
  }
}
