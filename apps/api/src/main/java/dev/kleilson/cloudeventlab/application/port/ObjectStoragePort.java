package dev.kleilson.cloudeventlab.application.port;

import java.util.Optional;

public interface ObjectStoragePort {

  String put(String key, byte[] content, String contentType);

  Optional<byte[]> get(String key);

  void delete(String key);
}
