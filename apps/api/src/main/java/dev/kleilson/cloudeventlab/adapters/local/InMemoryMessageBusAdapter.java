package dev.kleilson.cloudeventlab.adapters.local;

import dev.kleilson.cloudeventlab.application.port.MessageBusPort;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * In-process message bus for local/test profiles. OPEN SOURCE ALTERNATIVE to cloud queues — not a
 * cloud emulator.
 */
@Component
@Profile({"local", "default", "test"})
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
