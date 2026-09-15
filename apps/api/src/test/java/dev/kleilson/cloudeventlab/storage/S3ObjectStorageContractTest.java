package dev.kleilson.cloudeventlab.storage;

import dev.kleilson.cloudeventlab.adapters.aws.S3ObjectStorageAdapter;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Portability contract against LocalStack S3 (LOCAL EMULATOR). Skipped when Docker is unavailable.
 */
@Testcontainers(disabledWithoutDocker = true)
class S3ObjectStorageContractTest {

  @Container
  static LocalStackContainer localstack =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.3"))
          .withServices(LocalStackContainer.Service.S3);

  @Test
  void putGetDeleteRoundTrip() {
    S3Client s3 =
        S3Client.builder()
            .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
            .region(Region.of(localstack.getRegion()))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
            .forcePathStyle(true)
            .build();

    try (s3) {
      var storage = new S3ObjectStorageAdapter(s3, "cloud-event-lab-contract");
      ObjectStorageContractSupport.assertPutGetDeleteRoundTrip(storage);
    }
  }
}
