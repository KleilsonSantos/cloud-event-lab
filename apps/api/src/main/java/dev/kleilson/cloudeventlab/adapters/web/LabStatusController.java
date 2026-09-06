package dev.kleilson.cloudeventlab.adapters.web;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LabStatusController {

  @Value("${spring.profiles.active:default}")
  private String activeProfiles;

  @Value("${lab.cloud.provider:local}")
  private String cloudProvider;

  @GetMapping("/api/lab/status")
  public Map<String, Object> status() {
    return Map.of(
        "service", "cloud-event-lab-api",
        "cloudProvider", cloudProvider,
        "activeProfiles", activeProfiles,
        "honesty",
            "Emulators are LOCAL EMULATOR / APPROX — not production cloud parity");
  }
}
