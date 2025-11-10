package com.pixservice.application.wallet;

import com.pixservice.application.dto.DepositRequest;
import com.pixservice.application.dto.DepositResponse;
import com.pixservice.domain.Transaction;
import com.pixservice.domain.Wallet;
import com.pixservice.domain.enums.TransactionStatus;
import com.pixservice.domain.enums.TransactionType;
import com.pixservice.infrastructure.persistence.TransactionRepository;
import com.pixservice.infrastructure.persistence.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class DepositToWalletUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public DepositToWalletUseCase(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public DepositResponse execute(Long walletId, DepositRequest request) {

        log.info("Iniciando depósito | walletId={} | valor={}", walletId, request.amount());

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    log.error("Carteira não encontrada para depósito | walletId={}", walletId);
                    return new IllegalArgumentException("Wallet not found with ID: " + walletId);
                });

        wallet.setBalance(wallet.getBalance().add(request.amount()));
        walletRepository.save(wallet);
        log.info("Saldo atualizado com sucesso | walletId={} | novoSaldo={}", walletId, wallet.getBalance());

        Transaction transaction = new Transaction();
        transaction.setEndToEndId("DEP " + System.currentTimeMillis());
        transaction.setFromWallet(wallet);
        transaction.setToWallet(wallet);
        transaction.setAmount(request.amount());
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.CONFIRMED);
        transaction.setCreatedAt(Instant.now());
        transactionRepository.save(transaction);

        log.info("Transação de depósito registrada | endToEndId={} | walletId={} | valor={} | status={}",
                transaction.getEndToEndId(), walletId, request.amount(), transaction.getStatus());

        return new DepositResponse(wallet.getId(), request.amount(), "CONFIRMED", Instant.now());
    }
}
