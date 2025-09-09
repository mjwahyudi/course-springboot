package com.module3.demo1.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.module3.demo1.entity.Customer;
import com.module3.demo1.service.CustomerService;

@RestController
public class ApiController {
  private final CustomerService service;

  public ApiController(CustomerService service) {
    this.service = service;
  }
  
  @GetMapping("/greet")
  public String greet() {
    return "hello";
  }

  @GetMapping("/customers")
  public List<Customer> list() {
    return service.list();
  }
}
