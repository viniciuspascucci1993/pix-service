package com.pixservice.application.wallet;

import com.pixservice.application.dto.WalletBalanceHistoricalResponse;
import com.pixservice.domain.exceptions.NoTransactionHistoryException;
import com.pixservice.infrastructure.persistence.TransactionRepository;
import com.pixservice.infrastructure.persistence.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@Slf4j
public class GetWalletHistoricalBalanceUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public GetWalletHistoricalBalanceUseCase(WalletRepository walletRepository,
                                             TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public WalletBalanceHistoricalResponse execute(Long walletId, Instant at) {

        log.info("Consultando saldo histórico | walletId={} | até={}", walletId, at);

        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    log.error("Carteira não encontrada ao consultar histórico | walletId={}", walletId);
                    return new IllegalArgumentException("Wallet not found with ID: " + walletId);
                });

        Instant adjustedAt = at.plusMillis(999);

        var historicalBalance =
                transactionRepository.sumBalanceUntil(walletId, adjustedAt)
                .orElse(BigDecimal.ZERO);

        if (historicalBalance.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("⚠Nenhum histórico de transação encontrado | walletId={} | até={}", walletId, at);
            throw new NoTransactionHistoryException(walletId, at.toString());
        }

        log.info("Saldo histórico calculado | walletId={} | até={} | saldo={}", walletId, at, historicalBalance);

        return new WalletBalanceHistoricalResponse(wallet.getId(), historicalBalance, adjustedAt);
    }
}
