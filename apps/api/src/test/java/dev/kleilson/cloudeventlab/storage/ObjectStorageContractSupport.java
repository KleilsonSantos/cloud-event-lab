package dev.kleilson.cloudeventlab.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.kleilson.cloudeventlab.application.port.ObjectStoragePort;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Shared put → get → delete assertions for ObjectStoragePort adapters. */
final class ObjectStorageContractSupport {

  private ObjectStorageContractSupport() {}

  static void assertPutGetDeleteRoundTrip(ObjectStoragePort storage) {
    String key = "contract/" + UUID.randomUUID() + ".json";
    byte[] payload = "{\"phase\":2,\"ok\":true}".getBytes(StandardCharsets.UTF_8);

    assertEquals(key, storage.put(key, payload, "application/json"));
    assertArrayEquals(payload, storage.get(key).orElseThrow());

    storage.delete(key);
    assertTrue(storage.get(key).isEmpty());
  }
}
