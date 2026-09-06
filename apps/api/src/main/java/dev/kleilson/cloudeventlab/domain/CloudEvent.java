package dev.kleilson.cloudeventlab.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class CloudEvent {

  private final UUID id;
  private final String type;
  private final String source;
  private final String idempotencyKey;
  private final String payloadJson;
  private EventStatus status;
  private String storageKey;
  private String errorMessage;
  private final Instant createdAt;
  private Instant updatedAt;

  public CloudEvent(
      UUID id,
      String type,
      String source,
      String idempotencyKey,
      String payloadJson,
      EventStatus status,
      String storageKey,
      String errorMessage,
      Instant createdAt,
      Instant updatedAt) {
    this.id = Objects.requireNonNull(id);
    this.type = Objects.requireNonNull(type);
    this.source = Objects.requireNonNull(source);
    this.idempotencyKey = Objects.requireNonNull(idempotencyKey);
    this.payloadJson = Objects.requireNonNull(payloadJson);
    this.status = Objects.requireNonNull(status);
    this.storageKey = storageKey;
    this.errorMessage = errorMessage;
    this.createdAt = Objects.requireNonNull(createdAt);
    this.updatedAt = Objects.requireNonNull(updatedAt);
  }

  public static CloudEvent accept(
      String type, String source, String idempotencyKey, String payloadJson) {
    Instant now = Instant.now();
    return new CloudEvent(
        UUID.randomUUID(),
        type,
        source,
        idempotencyKey,
        payloadJson,
        EventStatus.ACCEPTED,
        null,
        null,
        now,
        now);
  }

  public void markQueued(String storageKey) {
    this.storageKey = storageKey;
    this.status = EventStatus.QUEUED;
    this.updatedAt = Instant.now();
  }

  public void markProcessing() {
    this.status = EventStatus.PROCESSING;
    this.updatedAt = Instant.now();
  }

  public void markProcessed() {
    this.status = EventStatus.PROCESSED;
    this.errorMessage = null;
    this.updatedAt = Instant.now();
  }

  public void markFailed(String message) {
    this.status = EventStatus.FAILED;
    this.errorMessage = message;
    this.updatedAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public String getSource() {
    return source;
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public String getPayloadJson() {
    return payloadJson;
  }

  public EventStatus getStatus() {
    return status;
  }

  public String getStorageKey() {
    return storageKey;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
