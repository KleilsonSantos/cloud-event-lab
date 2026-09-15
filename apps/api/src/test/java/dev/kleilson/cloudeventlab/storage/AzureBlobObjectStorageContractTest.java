package dev.kleilson.cloudeventlab.storage;

import com.azure.storage.blob.BlobServiceClientBuilder;
import dev.kleilson.cloudeventlab.adapters.azure.AzureBlobObjectStorageAdapter;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Portability contract against Azurite Blob (LOCAL EMULATOR). Skipped when Docker is unavailable.
 */
@Testcontainers(disabledWithoutDocker = true)
class AzureBlobObjectStorageContractTest {

  private static final String ACCOUNT = "devstoreaccount1";
  private static final String KEY =
      "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==";

  @Container
  @SuppressWarnings("resource")
  static GenericContainer<?> azurite =
      new GenericContainer<>(DockerImageName.parse("mcr.microsoft.com/azure-storage/azurite:3.34.0"))
          .withExposedPorts(10000)
          .withCommand("azurite-blob", "--blobHost", "0.0.0.0", "--loose");

  @Test
  void putGetDeleteRoundTrip() {
    String endpoint =
        "http://%s:%d/%s"
            .formatted(azurite.getHost(), azurite.getMappedPort(10000), ACCOUNT);
    String connection =
        "DefaultEndpointsProtocol=http;AccountName=%s;AccountKey=%s;BlobEndpoint=%s;"
            .formatted(ACCOUNT, KEY, endpoint);

    var containerClient =
        new BlobServiceClientBuilder()
            .connectionString(connection)
            .buildClient()
            .getBlobContainerClient("cloud-event-lab-contract");

    var storage = new AzureBlobObjectStorageAdapter(containerClient);
    ObjectStorageContractSupport.assertPutGetDeleteRoundTrip(storage);
  }
}
