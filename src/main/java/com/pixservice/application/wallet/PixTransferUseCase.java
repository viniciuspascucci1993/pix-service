package com.pixservice.application.wallet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pixservice.application.dto.PixTransferRequest;
import com.pixservice.application.dto.PixTransferResponse;
import com.pixservice.domain.IdempotencyKey;
import com.pixservice.domain.Transaction;
import com.pixservice.domain.enums.TransactionStatus;
import com.pixservice.domain.enums.TransactionType;
import com.pixservice.infrastructure.persistence.IdempotencyRepository;
import com.pixservice.infrastructure.persistence.PixKeyRepository;
import com.pixservice.infrastructure.persistence.TransactionRepository;
import com.pixservice.infrastructure.persistence.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class PixTransferUseCase {

    private final WalletRepository walletRepository;
    private final PixKeyRepository pixKeyRepository;
    private final TransactionRepository transactionRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;

    public PixTransferUseCase(WalletRepository walletRepository,
                              PixKeyRepository pixKeyRepository,
                              TransactionRepository transactionRepository,
                              IdempotencyRepository idempotencyRepository,
                              ObjectMapper objectMapper) {
        this.walletRepository = walletRepository;
        this.pixKeyRepository = pixKeyRepository;
        this.transactionRepository = transactionRepository;
        this.idempotencyRepository = idempotencyRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PixTransferResponse execute(String idempotencyKey, PixTransferRequest request) {

        // BUSCA CARTEIRAS
        var fromWallet = walletRepository.findById(request.fromWalletId())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with ID: " + request.fromWalletId()));

        var toPixKey = pixKeyRepository.findByKeyValue(request.toPixKey())
                .orElseThrow(() -> new IllegalArgumentException("Pix key not found: " + request.toPixKey()));

        var toWallet = toPixKey.getWallet();

        // Verifica idempotência POR carteira
        var existing = idempotencyRepository.findByWalletIdAndKeyValue(fromWallet.getId(), idempotencyKey);
        if (existing.isPresent()) {
            try {
                return objectMapper.readValue(existing.get().getResponseBody(), PixTransferResponse.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error parsing stored response", e);
            }
        }

        // VALIDAR SALDO
        if (fromWallet.getBalance().compareTo(request.amount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance for Pix transfer");
        }

        // CRIAR TRANSAÇÃO
        String endToEndId = "E2E-" + UUID.randomUUID();
        var transaction = new Transaction();
        transaction.setEndToEndId(endToEndId);
        transaction.setFromWallet(fromWallet);
        transaction.setToWallet(toWallet);
        transaction.setAmount(request.amount());
        transaction.setTransactionType(TransactionType.PIX_TRANSFER);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(Instant.now());
        transactionRepository.save(transaction);

        // DEBITAR
        fromWallet.setBalance(fromWallet.getBalance().subtract(request.amount()));
        walletRepository.save(fromWallet);

        var response = new PixTransferResponse(endToEndId, TransactionStatus.PENDING);

        try {
            var serialized = objectMapper.writeValueAsString(response);
            idempotencyRepository.save(new IdempotencyKey(fromWallet.getId(), idempotencyKey, serialized));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error saving idempotent response", e);
        }
        return response;
    }
}
