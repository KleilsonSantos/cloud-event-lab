package dev.kleilson.cloudeventlab.adapters.web;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lab-friendly alias for Actuator health (Phase 1 checklist uses {@code GET /health}).
 */
@RestController
public class HealthAliasController {

  @GetMapping("/health")
  public Map<String, String> health() {
    return Map.of("status", "UP");
  }
}
