package com.module2.demo1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class GreetingController {

  @GetMapping("/greet")
  public String greet() {
    return "hello";
  }

  @PostMapping("/welcome")
  public String postMethodName(@RequestBody String person) {
     return "welcome " + person;     
  }
  
}
