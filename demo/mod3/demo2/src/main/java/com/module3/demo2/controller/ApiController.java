package com.module3.demo2.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.module3.demo2.dto.TransferRequest;
import com.module3.demo2.entity.Account;
import com.module3.demo2.repository.AccountRepository;
import com.module3.demo2.service.AccountService;

@RestController
@RequestMapping("/api")
public class ApiController {
  private final AccountRepository accountRepo;
  private final AccountService accountService;

  public ApiController(AccountRepository accountRepo, AccountService accountService) {
    this.accountRepo = accountRepo;
    this.accountService = accountService;
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
    var b = accountRepo.save(new Account("Bob", new BigDecimal("500.00")));
    return List.of(a, b);
  }

  @GetMapping("/accounts/{id}/balance")
  public Map<String, Object> balance(@PathVariable Long id) {
    return Map.of("accountId", id, "balance", accountService.getBalance(id));
  }

  /*
   * Happy path transfer example
   *
   * curl -X POST localhost:8080/api/transfer/ok \
   * -H "Content-Type: application/json" \
   * -d '{"fromAccountId":1,"toAccountId":2,"amount":100.00}'
   * curl localhost:8080/api/accounts/1/balance # expect 900.00
   * curl localhost:8080/api/accounts/2/balance # expect 600.00
   */
  @PostMapping("/transfer/ok")
  public ResponseEntity<?> ok(@RequestBody TransferRequest req) {
    accountService.transferOk(req);
    return ResponseEntity.ok(Map.of("status", "OK"));
  }
}
