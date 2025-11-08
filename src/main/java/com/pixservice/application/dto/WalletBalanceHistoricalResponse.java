package com.pixservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletBalanceHistoricalResponse {

    private Long walletId;
    private BigDecimal balance;
    private Instant at;
}
