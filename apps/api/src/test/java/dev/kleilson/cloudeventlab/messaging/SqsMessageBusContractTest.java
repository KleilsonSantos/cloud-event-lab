package dev.kleilson.cloudeventlab.messaging;

import dev.kleilson.cloudeventlab.adapters.aws.SqsMessageBusAdapter;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

/** Portability contract against LocalStack SQS (LOCAL EMULATOR). */
@Testcontainers(disabledWithoutDocker = true)
class SqsMessageBusContractTest {

  @Container
  static LocalStackContainer localstack =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.3"))
          .withServices(LocalStackContainer.Service.SQS);

  @Test
  void publishSubscribeRoundTrip() throws Exception {
    SqsClient sqs =
        SqsClient.builder()
            .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.SQS))
            .region(Region.of(localstack.getRegion()))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        localstack.getAccessKey(), localstack.getSecretKey())))
            .build();

    try (sqs) {
      var bus = new SqsMessageBusAdapter(sqs, 1, 30);
      MessageBusContractSupport.assertPublishSubscribeRoundTrip(bus);
      bus.shutdown();
    }
  }
}
