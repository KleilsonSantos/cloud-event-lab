package dev.kleilson.cloudeventlab.application.port;

import java.util.Optional;

public interface SecretStorePort {

  Optional<String> getSecret(String name);
}
