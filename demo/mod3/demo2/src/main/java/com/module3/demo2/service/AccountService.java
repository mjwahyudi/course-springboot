package com.module3.demo2.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.module3.demo2.dto.TransferRequest;
import com.module3.demo2.repository.AccountRepository;

@Service
public class AccountService {
    private final AccountRepository accountRepo;
    private final AuditService auditService;

    public AccountService(AccountRepository accountRepo, AuditService auditService) {
        this.accountRepo = accountRepo;
        this.auditService = auditService;
    }

    // 5) Read-only example
    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long accountId) {
        return accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("account not found"))
                .getBalance();
    }

    // 1) Happy path: REQUIRED (default, may not declared explicitly)
    @Transactional(propagation = Propagation.REQUIRED)
    public void transferOk(TransferRequest req) {
        var from = accountRepo.findForUpdate(req.fromAccountId())
                .orElseThrow(() -> new IllegalArgumentException("from not found"));
        var to = accountRepo.findForUpdate(req.toAccountId())
                .orElseThrow(() -> new IllegalArgumentException("to not found"));

        ensureEnough(from.getBalance(), req.amount());

        from.debit(req.amount());
        to.credit(req.amount());

        // save is optional since managed entities are flushed at commit
        auditService.log(from.getId(), to.getId(), req.amount(), "OK", "transfer committed");
    }

    private void ensureEnough(BigDecimal balance, BigDecimal amt) {
        if (balance.compareTo(amt) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
    }

    // 2) Rollback on RuntimeException
    @Transactional
    public void transferFailRuntime(TransferRequest req) {
        var from = accountRepo.findForUpdate(req.fromAccountId())
                .orElseThrow();
        var to = accountRepo.findForUpdate(req.toAccountId())
                .orElseThrow();

        from.debit(req.amount());
        to.credit(req.amount());

        // inner audit logs in a new TX (will COMMIT even if outer fails)
        auditService.log(from.getId(), to.getId(), req.amount(), "FAIL", "outer runtime exception");

        // cause rollback of OUTER (required) transaction
        throw new IllegalStateException("Forcing rollback with runtime exception");
    }

    // 3) Checked exception: by default NOT rolled back (demo default behavior)
    @Transactional
    public void transferFailChecked_NoRollback(TransferRequest req) throws Exception {
        var from = accountRepo.findForUpdate(req.fromAccountId())
                .orElseThrow();
        var to = accountRepo.findForUpdate(req.toAccountId())
                .orElseThrow();

        from.debit(req.amount());
        to.credit(req.amount());

        auditService.log(from.getId(), to.getId(), req.amount(), "FAIL", "checked exception (no rollback)");
        throw new Exception("Checked exception -> default no rollback");
    }
}
