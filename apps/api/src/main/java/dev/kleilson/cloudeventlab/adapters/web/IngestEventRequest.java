package dev.kleilson.cloudeventlab.adapters.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IngestEventRequest(
    @NotBlank @Size(max = 128) String type,
    @NotBlank @Size(max = 256) String source,
    @NotBlank @Size(max = 128) String idempotencyKey,
    @NotBlank @Size(max = 100_000) String payloadJson) {}
