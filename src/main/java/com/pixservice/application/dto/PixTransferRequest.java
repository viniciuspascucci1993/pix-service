package com.pixservice.application.dto;

import java.math.BigDecimal;

public record PixTransferRequest(
        Long fromWalletId,
        String toPixKey,
        BigDecimal amount
) {}
