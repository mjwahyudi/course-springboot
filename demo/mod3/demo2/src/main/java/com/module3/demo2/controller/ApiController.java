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
   * Transfer $100 from Alice (1) to Bob (2)
   * End state: Alice $900, Bob $600
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

  /*
   * Transfer fails somehow
   * Transfer $50 from Alice (1) to Bob (2)
   * End state: Alice $900, Bob $600 (unchanged from previous)
   * But we still have an audit log of the failed transfer
   * 
   * curl -X POST localhost:8080/api/transfer/fail-runtime \
   * -H "Content-Type: application/json" \
   * -d '{"fromAccountId":1,"toAccountId":2,"amount":50.00}'
   * curl localhost:8080/api/accounts/1/balance # unchanged from previous (still
   * 900.00)
   * curl localhost:8080/api/accounts/2/balance # unchanged (still 600.00)
   * # Check DB table transfer_audits -> a FAIL row exists (committed in
   * REQUIRES_NEW)
   */
  @PostMapping("/transfer/fail-runtime")
  public ResponseEntity<?> failRuntime(@RequestBody TransferRequest req) {
    try {
      accountService.transferFailRuntime(req);
      return ResponseEntity.ok(Map.of("status", "unexpected"));
    } catch (RuntimeException ex) {
      return ResponseEntity.badRequest().body(Map.of("status", "ROLLED_BACK", "reason", ex.getMessage()));
    }
  }

  /*
   * Transfer fails with checked exception
   * Transfer $10 from Alice (1) to Bob (2)
   * End state: Alice $890, Bob $610 (CHANGED!)
   * 
   * curl -X POST localhost:8080/api/transfer/fail-checked-default \
   * -H "Content-Type: application/json" \
   * -d '{"fromAccountId":1,"toAccountId":2,"amount":10.00}'
   * curl localhost:8080/api/accounts/1/balance # 890.00 (changed!)
   * curl localhost:8080/api/accounts/2/balance # 610.00
   */
  @PostMapping("/transfer/fail-checked-default")
  public ResponseEntity<?> failCheckedDefault(@RequestBody TransferRequest req) {
    try {
      accountService.transferFailChecked_NoRollback(req);
      return ResponseEntity.ok(Map.of("status", "unexpected"));
    } catch (Exception ex) {
      return ResponseEntity.badRequest().body(Map.of("status", "MAYBE_COMMITTED", "reason", ex.getMessage()));
    }
  }

  /*
   * Transfer fails with checked exception but we force rollback
   * Transfer $10 from Alice (1) to Bob (2)
   * End state: Alice $890, Bob $610 (UNCHANGED from previous)
   * 
   * curl -X POST localhost:8080/api/transfer/fail-checked-rollbackfor \
   * -H "Content-Type: application/json" \
   * -d '{"fromAccountId":1,"toAccountId":2,"amount":10.00}'
   * curl localhost:8080/api/accounts/1/balance # still 890.00
   * curl localhost:8080/api/accounts/2/balance # still 610.00
   */
  @PostMapping("/transfer/fail-checked-rollbackfor")
  public ResponseEntity<?> failCheckedRollback(@RequestBody TransferRequest req) {
    try {
      accountService.transferFailChecked_WithRollback(req);
      return ResponseEntity.ok(Map.of("status", "unexpected"));
    } catch (Exception ex) {
      return ResponseEntity.badRequest().body(Map.of("status", "ROLLED_BACK", "reason", ex.getMessage()));
    }
  }

  /*
   * Transfer times out (sleeps too long)
   * Transfer $5 from Alice (1) to Bob (2)
   * End state: Alice $890, Bob $610 (UNCHANGED from previous)
   * 
   * curl -X POST localhost:8080/api/transfer/timeout \
   * -H "Content-Type: application/json" \
   * -d '{"fromAccountId":1,"toAccountId":2,"amount":5.00}'
   * # Expect HTTP 400 with timeout reason; balances unchanged afterward.
   */
  @PostMapping("/transfer/timeout")
  public ResponseEntity<?> timeout(@RequestBody TransferRequest req) {
    try {
      accountService.transferTimeout(req);
      return ResponseEntity.ok(Map.of("status", "unexpected"));
    } catch (Exception ex) {
      return ResponseEntity.badRequest().body(Map.of("status", "ROLLED_BACK_TIMEOUT", "reason", ex.getMessage()));
    }
  }
}
