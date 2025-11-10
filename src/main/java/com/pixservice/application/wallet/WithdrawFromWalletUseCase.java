package com.pixservice.application.wallet;

import com.pixservice.application.dto.WithdrawRequest;
import com.pixservice.application.dto.WithdrawResponse;
import com.pixservice.domain.Transaction;
import com.pixservice.domain.Wallet;
import com.pixservice.domain.enums.TransactionStatus;
import com.pixservice.domain.enums.TransactionType;
import com.pixservice.infrastructure.persistence.TransactionRepository;
import com.pixservice.infrastructure.persistence.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@Slf4j
public class WithdrawFromWalletUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WithdrawFromWalletUseCase(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public WithdrawResponse execute(Long walletId, WithdrawRequest request) {

        log.info("Iniciando solicitação de saque | walletId={} | valor={}", walletId, request.amount());

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    log.error("Falha ao processar saque: carteira não encontrada | walletId={}", walletId);
                    return new IllegalArgumentException("Wallet not found with ID: " + walletId);
                });

        BigDecimal amount = request.amount();

        if (wallet.getBalance().compareTo(amount) < 0) {
            log.error("Saldo insuficiente para saque | walletId={} | saldoAtual={} | solicitado={}",
                    walletId, wallet.getBalance(), amount);
            throw new IllegalArgumentException("Insufficient balance for withdrawal");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
        log.error("Saldo insuficiente para saque | walletId={} | saldoAtual={} | solicitado={}",
                walletId, wallet.getBalance(), amount);

        Transaction transaction = new Transaction();
        transaction.setEndToEndId("WTD-" + System.currentTimeMillis());
        transaction.setFromWallet(wallet);
        transaction.setToWallet(null); // saída externa
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setStatus(TransactionStatus.CONFIRMED);
        transaction.setCreatedAt(Instant.now());
        transactionRepository.save(transaction);

        log.info("Saque concluído com sucesso | walletId={} | transacaoId={} | valor={} | status={}",
                walletId, transaction.getEndToEndId(), amount, transaction.getStatus());

        return new WithdrawResponse(wallet.getId(), wallet.getBalance(), amount, Instant.now());
    }

}
