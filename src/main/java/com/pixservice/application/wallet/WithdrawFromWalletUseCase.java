package com.pixservice.application.wallet;

import com.pixservice.application.dto.WithdrawRequest;
import com.pixservice.application.dto.WithdrawResponse;
import com.pixservice.domain.Transaction;
import com.pixservice.domain.Wallet;
import com.pixservice.domain.enums.TransactionStatus;
import com.pixservice.domain.enums.TransactionType;
import com.pixservice.infrastructure.persistence.TransactionRepository;
import com.pixservice.infrastructure.persistence.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class WithdrawFromWalletUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WithdrawFromWalletUseCase(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public WithdrawResponse execute(Long walletId, WithdrawRequest request) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with ID: " + walletId));

        BigDecimal amount = request.amount();

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance for withdrawal");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setEndToEndId("WTD-" + System.currentTimeMillis());
        transaction.setFromWallet(wallet);
        transaction.setToWallet(null); // saída externa
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setStatus(TransactionStatus.CONFIRMED);
        transaction.setCreatedAt(Instant.now());
        transactionRepository.save(transaction);

        return new WithdrawResponse(wallet.getId(), wallet.getBalance(), amount, Instant.now());
    }

}
