package com.pixservice.application.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record WithdrawResponse(
        Long walletId,
        BigDecimal newBalance,
        BigDecimal amount,
        Instant timestamp
) {
}
