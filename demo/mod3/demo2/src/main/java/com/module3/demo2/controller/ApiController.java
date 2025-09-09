package com.module3.demo2.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.module3.demo2.entity.Account;
import com.module3.demo2.repository.AccountRepository;

@RestController
@RequestMapping("/api")
public class ApiController {
  private final AccountRepository accountRepo;

  public ApiController(AccountRepository accountRepo) {
    this.accountRepo = accountRepo;
  }
  
  @GetMapping("/greet")
  public String greet() {
    return "hello";
  }

    // seed accounts quickly
  @PostMapping("/accounts/seed")
  public List<Account> seed() {
    accountRepo.deleteAll();
    var a = accountRepo.save(new Account("Alice", new BigDecimal("1000.00")));
    var b = accountRepo.save(new Account("Bob",   new BigDecimal("500.00")));
    return List.of(a, b);
  }
}
