package dev.kleilson.cloudeventlab.adapters.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataCloudEventRepository extends JpaRepository<CloudEventEntity, UUID> {

  Optional<CloudEventEntity> findByIdempotencyKey(String idempotencyKey);

  @Query("select e from CloudEventEntity e order by e.createdAt desc")
  List<CloudEventEntity> findRecent();
}
