package dev.kleilson.cloudeventlab.adapters.azure;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.specialized.BlockBlobClient;
import dev.kleilson.cloudeventlab.application.port.ObjectStoragePort;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Object storage via Azure Blob against Azurite (LOCAL EMULATOR) or a real connection string.
 * Not Azure production parity — see docs/azure/README.md.
 */
@Component
@Profile("azure")
@ConditionalOnProperty(name = "lab.cloud.provider", havingValue = "azure")
public class AzureBlobObjectStorageAdapter implements ObjectStoragePort {

  private final BlobContainerClient container;

  public AzureBlobObjectStorageAdapter(
      @Value("${lab.azure.storage-connection}") String connectionString,
      @Value("${lab.azure.blob.container:cloud-event-lab}") String containerName) {
    this(
        new BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient()
            .getBlobContainerClient(containerName));
  }

  /** Test / manual wiring without Spring. */
  public AzureBlobObjectStorageAdapter(BlobContainerClient container) {
    this.container = container;
    if (!this.container.exists()) {
      this.container.create();
    }
  }

  @Override
  public String put(String key, byte[] content, String contentType) {
    BlockBlobClient blob = container.getBlobClient(key).getBlockBlobClient();
    String type = contentType == null ? "application/octet-stream" : contentType;
    blob.upload(new ByteArrayInputStream(content), content.length, true);
    blob.setHttpHeaders(
        new com.azure.storage.blob.models.BlobHttpHeaders().setContentType(type));
    return key;
  }

  @Override
  public Optional<byte[]> get(String key) {
    try {
      BlockBlobClient blob = container.getBlobClient(key).getBlockBlobClient();
      if (!blob.exists()) {
        return Optional.empty();
      }
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      blob.downloadStream(out);
      return Optional.of(out.toByteArray());
    } catch (BlobStorageException e) {
      if (e.getStatusCode() == 404) {
        return Optional.empty();
      }
      throw e;
    }
  }

  @Override
  public void delete(String key) {
    container.getBlobClient(key).deleteIfExists();
  }
}
