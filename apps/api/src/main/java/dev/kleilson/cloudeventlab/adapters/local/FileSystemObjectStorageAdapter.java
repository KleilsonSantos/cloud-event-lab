package dev.kleilson.cloudeventlab.adapters.local;

import dev.kleilson.cloudeventlab.application.port.ObjectStoragePort;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"local", "default", "test"})
public class FileSystemObjectStorageAdapter implements ObjectStoragePort {

  private final Path root;

  public FileSystemObjectStorageAdapter(
      @Value("${lab.storage.fs.root:./data/object-storage}") String rootPath) throws IOException {
    this.root = Path.of(rootPath).toAbsolutePath().normalize();
    Files.createDirectories(this.root);
  }

  @Override
  public String put(String key, byte[] content, String contentType) {
    try {
      Path target = resolve(key);
      Files.createDirectories(target.getParent());
      Files.write(target, content);
      return key;
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write object: " + key, e);
    }
  }

  @Override
  public Optional<byte[]> get(String key) {
    try {
      Path target = resolve(key);
      if (!Files.exists(target)) {
        return Optional.empty();
      }
      return Optional.of(Files.readAllBytes(target));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read object: " + key, e);
    }
  }

  @Override
  public void delete(String key) {
    try {
      Files.deleteIfExists(resolve(key));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to delete object: " + key, e);
    }
  }

  private Path resolve(String key) {
    Path resolved = root.resolve(key).normalize();
    if (!resolved.startsWith(root)) {
      throw new IllegalArgumentException("Invalid storage key");
    }
    return resolved;
  }
}
