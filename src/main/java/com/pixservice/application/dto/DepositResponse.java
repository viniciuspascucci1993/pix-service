package com.pixservice.application.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record DepositResponse(
        Long walletId,
        BigDecimal amount,
        String status,
        Instant timestamp
) { }
