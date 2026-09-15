package dev.kleilson.cloudeventlab.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.kleilson.cloudeventlab.application.port.MessageBusPort;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

final class MessageBusContractSupport {

  private MessageBusContractSupport() {}

  static void assertPublishSubscribeRoundTrip(MessageBusPort bus) throws InterruptedException {
    String queue = "cloud-events-contract-" + UUID.randomUUID().toString().substring(0, 8);
    String payload = "msg-" + UUID.randomUUID();
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<String> received = new AtomicReference<>();

    bus.subscribe(
        queue,
        body -> {
          received.set(body);
          latch.countDown();
        });

    // Give pollers a moment to start (cloud adapters).
    Thread.sleep(300);
    bus.publish(queue, payload);

    assertTrue(latch.await(20, TimeUnit.SECONDS), "timed out waiting for message");
    assertEquals(payload, received.get());
  }
}
