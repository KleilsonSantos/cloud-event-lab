package dev.kleilson.cloudeventlab.adapters.web;

import dev.kleilson.cloudeventlab.application.EventApplicationService;
import dev.kleilson.cloudeventlab.domain.CloudEvent;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/events")
public class EventController {

  private final EventApplicationService events;

  public EventController(EventApplicationService events) {
    this.events = events;
  }

  @PostMapping
  public ResponseEntity<EventResponse> ingest(@Valid @RequestBody IngestEventRequest request) {
    CloudEvent created =
        events.ingest(
            request.type(), request.source(), request.idempotencyKey(), request.payloadJson());
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(toResponse(created));
  }

  @GetMapping("/{id}")
  public EventResponse get(@PathVariable UUID id) {
    return events
        .get(id)
        .map(this::toResponse)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @GetMapping
  public List<EventResponse> list(@RequestParam(defaultValue = "20") int limit) {
    return events.listRecent(limit).stream().map(this::toResponse).toList();
  }

  private EventResponse toResponse(CloudEvent e) {
    return new EventResponse(
        e.getId(),
        e.getType(),
        e.getSource(),
        e.getIdempotencyKey(),
        e.getStatus().name(),
        e.getStorageKey(),
        e.getErrorMessage(),
        e.getCreatedAt(),
        e.getUpdatedAt());
  }
}
