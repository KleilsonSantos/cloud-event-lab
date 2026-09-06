package dev.kleilson.cloudeventlab.adapters.persistence;

import dev.kleilson.cloudeventlab.application.port.EventRepositoryPort;
import dev.kleilson.cloudeventlab.domain.CloudEvent;
import dev.kleilson.cloudeventlab.domain.EventStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JpaEventRepositoryAdapter implements EventRepositoryPort {

  private final SpringDataCloudEventRepository repo;

  public JpaEventRepositoryAdapter(SpringDataCloudEventRepository repo) {
    this.repo = repo;
  }

  @Override
  public CloudEvent save(CloudEvent event) {
    return toDomain(repo.save(toEntity(event)));
  }

  @Override
  public Optional<CloudEvent> findById(UUID id) {
    return repo.findById(id).map(this::toDomain);
  }

  @Override
  public Optional<CloudEvent> findByIdempotencyKey(String idempotencyKey) {
    return repo.findByIdempotencyKey(idempotencyKey).map(this::toDomain);
  }

  @Override
  public List<CloudEvent> findRecent(int limit) {
    return repo.findRecent().stream().limit(limit).map(this::toDomain).toList();
  }

  private CloudEventEntity toEntity(CloudEvent event) {
    CloudEventEntity e = new CloudEventEntity();
    e.setId(event.getId());
    e.setType(event.getType());
    e.setSource(event.getSource());
    e.setIdempotencyKey(event.getIdempotencyKey());
    e.setPayloadJson(event.getPayloadJson());
    e.setStatus(EventStatusJpa.valueOf(event.getStatus().name()));
    e.setStorageKey(event.getStorageKey());
    e.setErrorMessage(event.getErrorMessage());
    e.setCreatedAt(event.getCreatedAt());
    e.setUpdatedAt(event.getUpdatedAt());
    return e;
  }

  private CloudEvent toDomain(CloudEventEntity e) {
    return new CloudEvent(
        e.getId(),
        e.getType(),
        e.getSource(),
        e.getIdempotencyKey(),
        e.getPayloadJson(),
        EventStatus.valueOf(e.getStatus().name()),
        e.getStorageKey(),
        e.getErrorMessage(),
        e.getCreatedAt(),
        e.getUpdatedAt());
  }
}
