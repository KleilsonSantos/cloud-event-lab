package dev.kleilson.cloudeventlab.messaging;

import dev.kleilson.cloudeventlab.adapters.local.InMemoryMessageBusAdapter;
import org.junit.jupiter.api.Test;

class InMemoryMessageBusContractTest {

  @Test
  void publishSubscribeRoundTrip() throws Exception {
    MessageBusContractSupport.assertPublishSubscribeRoundTrip(new InMemoryMessageBusAdapter());
  }
}
