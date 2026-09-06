package dev.kleilson.cloudeventlab.adapters.local;

import dev.kleilson.cloudeventlab.application.port.SecretStorePort;
import java.util.Optional;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/** Env/file-backed secrets for local labs. Not a Key Vault / Secrets Manager emulator. */
@Component
public class EnvSecretStoreAdapter implements SecretStorePort {

  private final Environment env;

  public EnvSecretStoreAdapter(Environment env) {
    this.env = env;
  }

  @Override
  public Optional<String> getSecret(String name) {
    return Optional.ofNullable(env.getProperty(name))
        .or(() -> Optional.ofNullable(env.getProperty(name.replace('.', '_').toUpperCase())));
  }
}
