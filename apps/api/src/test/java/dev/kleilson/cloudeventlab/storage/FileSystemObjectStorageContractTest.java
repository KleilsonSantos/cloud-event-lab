package dev.kleilson.cloudeventlab.storage;

import dev.kleilson.cloudeventlab.adapters.local.FileSystemObjectStorageAdapter;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileSystemObjectStorageContractTest {

  @TempDir java.nio.file.Path tempDir;

  @Test
  void putGetDeleteRoundTrip() throws Exception {
    Files.createDirectories(tempDir);
    var storage = new FileSystemObjectStorageAdapter(tempDir.toString());
    ObjectStorageContractSupport.assertPutGetDeleteRoundTrip(storage);
  }
}
