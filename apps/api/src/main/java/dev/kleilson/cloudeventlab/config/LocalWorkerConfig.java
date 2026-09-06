package dev.kleilson.cloudeventlab.config;

import dev.kleilson.cloudeventlab.application.EventApplicationService;
import dev.kleilson.cloudeventlab.application.port.MessageBusPort;
import jakarta.annotation.PostConstruct;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"local", "default", "test"})
public class LocalWorkerConfig {

  private static final Logger log = LoggerFactory.getLogger(LocalWorkerConfig.class);

  private final MessageBusPort bus;
  private final EventApplicationService events;

  public LocalWorkerConfig(MessageBusPort bus, EventApplicationService events) {
    this.bus = bus;
    this.events = events;
  }

  @PostConstruct
  void subscribe() {
    bus.subscribe(
        EventApplicationService.QUEUE_EVENTS,
        body -> {
          try {
            events.process(UUID.fromString(body));
          } catch (Exception ex) {
            log.error("Worker failed for message={}", body, ex);
          }
        });
  }
}
