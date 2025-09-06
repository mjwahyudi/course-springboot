package com.module1.demo4.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

  @Value("${greeting.message:No Message Found}")  
  private String message;
  
  @Value("${greeting.audience:No Audience Found}")
  private String audience;

  @GetMapping("/greet")
  public Map<String, String> greet() {
    return Map.of(
      "message", message,
      "audience", audience
    );
  }
}
