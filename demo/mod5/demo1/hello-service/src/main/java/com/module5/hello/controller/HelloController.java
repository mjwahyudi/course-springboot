package com.module5.hello.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  @Value("${greeting.message:Hello (local fallback)}")
  private String message;

  @Value("${spring.application.name}")
  private String appName;

  @Value("${server.port}")
  private String port;

  @GetMapping("/hello")
  public Map<String, String> hello() {
    return Map.of(
      "service", appName,
      "port", port,
      "message", message,
      "timestamp", Instant.now().toString()
    );
  }
}
