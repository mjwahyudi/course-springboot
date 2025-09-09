package com.module3.demo2.dto;

import java.math.BigDecimal;

public record TransferRequest(Long fromAccountId, Long toAccountId, BigDecimal amount) {
}
