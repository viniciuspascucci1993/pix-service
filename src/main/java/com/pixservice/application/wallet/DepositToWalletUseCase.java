package com.pixservice.application.wallet;

import com.pixservice.application.dto.DepositRequest;
import com.pixservice.application.dto.DepositResponse;
import com.pixservice.domain.Transaction;
import com.pixservice.domain.Wallet;
import com.pixservice.domain.enums.TransactionStatus;
import com.pixservice.domain.enums.TransactionType;
import com.pixservice.infrastructure.persistence.TransactionRepository;
import com.pixservice.infrastructure.persistence.WalletRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class DepositToWalletUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public DepositToWalletUseCase(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public DepositResponse execute(Long walletId, DepositRequest request) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with ID: " + walletId));

        wallet.setBalance(wallet.getBalance().add(request.amount()));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setEndToEndId("DEP " + System.currentTimeMillis());
        transaction.setFromWallet(wallet);
        transaction.setToWallet(wallet);
        transaction.setAmount(request.amount());
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.CONFIRMED);
        transaction.setCreatedAt(Instant.now());
        transactionRepository.save(transaction);

        return new DepositResponse(wallet.getId(), request.amount(), "CONFIRMED", Instant.now());
    }
}
