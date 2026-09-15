package dev.kleilson.cloudeventlab.messaging;

import dev.kleilson.cloudeventlab.adapters.azure.AzureBlobObjectStorageAdapter;
import dev.kleilson.cloudeventlab.adapters.azure.AzureQueueMessageBusAdapter;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/** Portability contract against Azurite Queue (LOCAL EMULATOR). */
@Testcontainers(disabledWithoutDocker = true)
class AzureQueueMessageBusContractTest {

  @Container
  @SuppressWarnings("resource")
  static GenericContainer<?> azurite =
      new GenericContainer<>(DockerImageName.parse("mcr.microsoft.com/azure-storage/azurite:3.34.0"))
          .withExposedPorts(10001)
          .withCommand("azurite-queue", "--queueHost", "0.0.0.0", "--loose");

  @Test
  void publishSubscribeRoundTrip() throws Exception {
    String endpoint =
        "http://%s:%d/devstoreaccount1"
            .formatted(azurite.getHost(), azurite.getMappedPort(10001));
    String connection =
        AzureBlobObjectStorageAdapter.resolveQueueConnection(
            "UseDevelopmentStorage=true", endpoint);

    var bus = new AzureQueueMessageBusAdapter(connection);
    MessageBusContractSupport.assertPublishSubscribeRoundTrip(bus);
    bus.shutdown();
  }
}
