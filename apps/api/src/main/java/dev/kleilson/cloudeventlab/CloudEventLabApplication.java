package dev.kleilson.cloudeventlab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CloudEventLabApplication {

  public static void main(String[] args) {
    SpringApplication.run(CloudEventLabApplication.class, args);
  }
}
