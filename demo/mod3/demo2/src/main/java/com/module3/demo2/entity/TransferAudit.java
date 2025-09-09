package com.module3.demo2.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "transfer_audits")
public class TransferAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromAccountId;
    private Long toAccountId;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    private String status; // OK / FAIL
    @Column(length = 500)
    private String message;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    public TransferAudit() {
    }

    public TransferAudit(Long fromId, Long toId, BigDecimal amount, String status, String message) {
        this.fromAccountId = fromId;
        this.toAccountId = toId;
        this.amount = amount;
        this.status = status;
        this.message = message;
    }

    // getters & setters
    public Long getId() {
        return id;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFromAccountId(Long v) {
        this.fromAccountId = v;
    }

    public void setToAccountId(Long v) {
        this.toAccountId = v;
    }

    public void setAmount(BigDecimal v) {
        this.amount = v;
    }

    public void setStatus(String v) {
        this.status = v;
    }

    public void setMessage(String v) {
        this.message = v;
    }

    public void setCreatedAt(OffsetDateTime v) {
        this.createdAt = v;
    }
}
