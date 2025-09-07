package com.module2.demo4.controller;

import org.springframework.web.bind.annotation.RestController;

import com.module2.demo4.dto.UserDto;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@RestController
public class GreetingController {

  @PostMapping("/welcome")
  public String postMethodName(@Valid @RequestBody UserDto user) {
    return "welcome " + user.getEmail();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleBodyErrors(MethodArgumentNotValidException ex) {
    String msg = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage()
            + " (rejected=" + fe.getRejectedValue() + ")")
        .collect(Collectors.joining("\n"));
    return ResponseEntity.badRequest().body("Validation Failed:\n" + msg);
  }
}
