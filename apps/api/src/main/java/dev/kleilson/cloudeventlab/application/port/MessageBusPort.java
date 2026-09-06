package dev.kleilson.cloudeventlab.application.port;

import java.util.UUID;
import java.util.function.Consumer;

public interface MessageBusPort {

  void publish(String topicOrQueue, String messageBody);

  /** Local/in-process subscription. Cloud adapters may use polling workers instead. */
  void subscribe(String topicOrQueue, Consumer<String> handler);

  record EventMessage(UUID eventId) {}
}
