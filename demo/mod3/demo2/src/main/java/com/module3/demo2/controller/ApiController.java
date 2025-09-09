package com.module3.demo2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
  @GetMapping("/greet")
  public String greet() {
    return "hello";
  }
}
