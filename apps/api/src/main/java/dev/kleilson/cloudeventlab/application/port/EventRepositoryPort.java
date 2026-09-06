package dev.kleilson.cloudeventlab.application.port;

import dev.kleilson.cloudeventlab.domain.CloudEvent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepositoryPort {

  CloudEvent save(CloudEvent event);

  Optional<CloudEvent> findById(UUID id);

  Optional<CloudEvent> findByIdempotencyKey(String idempotencyKey);

  List<CloudEvent> findRecent(int limit);
}
