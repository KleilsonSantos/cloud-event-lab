package dev.kleilson.cloudeventlab.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CloudEventTest {

  @Test
  void acceptStartsAsAccepted() {
    CloudEvent event = CloudEvent.accept("demo.created", "lab", "key-1", "{}");
    assertEquals(EventStatus.ACCEPTED, event.getStatus());
    event.markQueued("events/1.json");
    assertEquals(EventStatus.QUEUED, event.getStatus());
    event.markProcessing();
    event.markProcessed();
    assertEquals(EventStatus.PROCESSED, event.getStatus());
  }
}
