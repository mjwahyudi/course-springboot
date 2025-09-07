package com.module2.demo1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



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
  
  @PutMapping("/give/{person}")
  public String putMethodName(@PathVariable String person, @RequestBody String baggage) {
     return "here your " + baggage +", Mr:" + person;   
  }

}
