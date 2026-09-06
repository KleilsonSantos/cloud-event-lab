package dev.kleilson.cloudeventlab.application;

import dev.kleilson.cloudeventlab.application.port.EventRepositoryPort;
import dev.kleilson.cloudeventlab.application.port.MessageBusPort;
import dev.kleilson.cloudeventlab.application.port.ObjectStoragePort;
import dev.kleilson.cloudeventlab.domain.CloudEvent;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class EventApplicationService {

  public static final String QUEUE_EVENTS = "cloud-events";

  private static final Logger log = LoggerFactory.getLogger(EventApplicationService.class);

  private final EventRepositoryPort events;
  private final ObjectStoragePort storage;
  private final MessageBusPort bus;

  public EventApplicationService(
      EventRepositoryPort events, ObjectStoragePort storage, MessageBusPort bus) {
    this.events = events;
    this.storage = storage;
    this.bus = bus;
  }

  @Transactional
  public CloudEvent ingest(String type, String source, String idempotencyKey, String payloadJson) {
    Optional<CloudEvent> existing = events.findByIdempotencyKey(idempotencyKey);
    if (existing.isPresent()) {
      log.info("Idempotent hit for key={}", idempotencyKey);
      return existing.get();
    }

    CloudEvent event = CloudEvent.accept(type, source, idempotencyKey, payloadJson);
    String key = "events/" + event.getId() + ".json";
    storage.put(key, payloadJson.getBytes(StandardCharsets.UTF_8), "application/json");
    event.markQueued(key);
    CloudEvent saved = events.save(event);
    publishAfterCommit(QUEUE_EVENTS, saved.getId().toString());
    return saved;
  }

  private void publishAfterCommit(String queue, String body) {
    if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      bus.publish(queue, body);
      return;
    }
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            bus.publish(queue, body);
          }
        });
  }

  @Transactional
  public void process(UUID eventId) {
    CloudEvent event =
        events
            .findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

    if (event.getStatus().name().equals("PROCESSED")) {
      log.info("Skip already processed eventId={}", eventId);
      return;
    }

    try {
      event.markProcessing();
      events.save(event);

      if (event.getStorageKey() != null) {
        storage
            .get(event.getStorageKey())
            .orElseThrow(() -> new IllegalStateException("Payload missing in storage"));
      }

      // Business placeholder: validate payload exists and mark processed.
      event.markProcessed();
      events.save(event);
      log.info("Processed eventId={}", eventId);
    } catch (RuntimeException ex) {
      event.markFailed(ex.getMessage());
      events.save(event);
      throw ex;
    }
  }

  @Transactional(readOnly = true)
  public Optional<CloudEvent> get(UUID id) {
    return events.findById(id);
  }

  @Transactional(readOnly = true)
  public List<CloudEvent> listRecent(int limit) {
    return events.findRecent(Math.min(Math.max(limit, 1), 100));
  }
}
