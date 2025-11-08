package com.pixservice.application.wallet;

import com.pixservice.application.dto.WalletBalanceHistoricalResponse;
import com.pixservice.domain.exceptions.NoTransactionHistoryException;
import com.pixservice.infrastructure.persistence.TransactionRepository;
import com.pixservice.infrastructure.persistence.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class GetWalletHistoricalBalanceUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public GetWalletHistoricalBalanceUseCase(WalletRepository walletRepository,
                                             TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public WalletBalanceHistoricalResponse execute(Long walletId, Instant at) {

        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with ID: " + walletId));

        var historicalBalance = transactionRepository.sumBalanceUntil(walletId, at)
                .orElse(BigDecimal.ZERO);

        if (historicalBalance.compareTo(BigDecimal.ZERO) == 0) {
            throw new NoTransactionHistoryException(walletId, at.toString());
        }

        return new WalletBalanceHistoricalResponse(wallet.getId(), historicalBalance, at);
    }
}
