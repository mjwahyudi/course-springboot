package com.module3.demo2.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.module3.demo2.entity.TransferAudit;
import com.module3.demo2.repository.TransferAuditRepository;

@Service
public class AuditService {
    private final TransferAuditRepository auditRepo;

    public AuditService(TransferAuditRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    @Transactional
    public void log(Long fromId, Long toId, BigDecimal amount, String status, String msg) {
        auditRepo.save(new TransferAudit(fromId, toId, amount, status, msg));
    }
}
