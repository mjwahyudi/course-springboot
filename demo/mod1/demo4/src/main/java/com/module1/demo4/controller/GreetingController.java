package com.module1.demo4.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.module1.demo4.Service.GreetingService;

@RestController
public class GreetingController {

  @Autowired
  private GreetingService greetingService;

  @GetMapping("/greet")
  public String greet() {
    return greetingService.getGreetingMessage();
  }
}
