package dev.kleilson.cloudeventlab.messaging;

import dev.kleilson.cloudeventlab.adapters.gcp.PubSubMessageBusAdapter;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/** Portability contract against Pub/Sub emulator (LOCAL EMULATOR). */
@Testcontainers(disabledWithoutDocker = true)
class PubSubMessageBusContractTest {

  @Container
  @SuppressWarnings("resource")
  static GenericContainer<?> pubsub =
      new GenericContainer<>(
              DockerImageName.parse("gcr.io/google.com/cloudsdktool/google-cloud-cli:516.0.0-emulators"))
          .withExposedPorts(8085)
          .withCommand(
              "gcloud", "beta", "emulators", "pubsub", "start", "--host-port=0.0.0.0:8085");

  @Test
  void publishSubscribeRoundTrip() throws Exception {
    String host = pubsub.getHost() + ":" + pubsub.getMappedPort(8085);
    var bus = new PubSubMessageBusAdapter("cloud-event-lab-contract", host);
    MessageBusContractSupport.assertPublishSubscribeRoundTrip(bus);
    bus.shutdown();
  }
}
