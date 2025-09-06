package com.module1.demo4.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.module1.demo4.configuration.GreetingProperties;

@RestController
public class GreetingController {

  @Autowired
  private GreetingProperties greetingProperties;

  @GetMapping("/greet")
  public Map<String, String> greet() {
    return Map.of(
      "message", greetingProperties.getMessage(),
      "audience", greetingProperties.getAudience(),
      "enabled", String.valueOf(greetingProperties.isEnabled())
    );
  }
}
