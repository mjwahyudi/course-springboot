package com.module2.demo4.controller;

import org.springframework.web.bind.annotation.RestController;

import com.module2.demo4.dto.UserDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@RestController
public class GreetingController {

  @PostMapping("/welcome")
  public String postMethodName(@Valid @RequestBody UserDto user) {
    return "welcome " + user.getEmail();
  }
}
