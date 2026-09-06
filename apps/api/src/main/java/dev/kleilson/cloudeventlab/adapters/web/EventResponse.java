package dev.kleilson.cloudeventlab.adapters.web;

import java.time.Instant;
import java.util.UUID;

public record EventResponse(
    UUID id,
    String type,
    String source,
    String idempotencyKey,
    String status,
    String storageKey,
    String errorMessage,
    Instant createdAt,
    Instant updatedAt) {}
