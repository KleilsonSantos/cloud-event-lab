package dev.kleilson.cloudeventlab.adapters.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cloud_events")
public class CloudEventEntity {

  @Id
  private UUID id;

  @Column(nullable = false, length = 128)
  private String type;

  @Column(nullable = false, length = 256)
  private String source;

  @Column(name = "idempotency_key", nullable = false, unique = true, length = 128)
  private String idempotencyKey;

  @Lob
  @Column(name = "payload_json", nullable = false)
  private String payloadJson;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private EventStatusJpa status;

  @Column(name = "storage_key", length = 512)
  private String storageKey;

  @Column(name = "error_message", length = 1024)
  private String errorMessage;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public void setIdempotencyKey(String idempotencyKey) {
    this.idempotencyKey = idempotencyKey;
  }

  public String getPayloadJson() {
    return payloadJson;
  }

  public void setPayloadJson(String payloadJson) {
    this.payloadJson = payloadJson;
  }

  public EventStatusJpa getStatus() {
    return status;
  }

  public void setStatus(EventStatusJpa status) {
    this.status = status;
  }

  public String getStorageKey() {
    return storageKey;
  }

  public void setStorageKey(String storageKey) {
    this.storageKey = storageKey;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}
