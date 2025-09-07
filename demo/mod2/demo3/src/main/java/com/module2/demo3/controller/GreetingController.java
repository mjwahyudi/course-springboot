package com.module2.demo3.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.module2.demo3.dto.UserDto;

import jakarta.validation.Valid;

@RestController
public class GreetingController {

  @PostMapping("/welcome")
  public String postMethodName(@Valid @RequestBody UserDto user) {
    return "welcome " + user.getEmail();
  }
}
