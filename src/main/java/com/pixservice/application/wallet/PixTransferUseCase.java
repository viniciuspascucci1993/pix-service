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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
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

        log.info("Iniiando serviço de Pix Transfer | wallet={} | toPix={} | amount={} | idemKey={}",
                request.fromWalletId(), request.toPixKey(), request.amount(), idempotencyKey);

        // BUSCA CARTEIRAS
        var fromWallet = walletRepository.findById(request.fromWalletId())
                .orElseThrow(() -> {
                    log.error("Wallet origem não encontrada: {}", request.fromWalletId());
                    return new IllegalArgumentException("Wallet not found with ID: " + request.fromWalletId());
                });

        var toPixKey = pixKeyRepository.findByKeyValue(request.toPixKey())
                .orElseThrow(() -> {
                    log.error("PixKey destino não encontrada: {}", request.toPixKey());
                    return new IllegalArgumentException("Pix key not found: " + request.toPixKey());
                });

        var toWallet = toPixKey.getWallet();

        // Verifica idempotência POR carteira
        var existing = idempotencyRepository.findByWalletIdAndKeyValue(fromWallet.getId(), idempotencyKey);
        if (existing.isPresent()) {
            try {
                log.warn("Requisição idempotente detectada (reuse) | walletId={} | key={}", fromWallet.getId(), idempotencyKey);
                return objectMapper.readValue(existing.get().getResponseBody(), PixTransferResponse.class);
            } catch (JsonProcessingException e) {
                log.error("Erro ao desserializar resposta armazenada para idempotência: {}", e.getMessage());
                throw new RuntimeException("Error parsing stored response", e);
            }
        }

        // VALIDAR SALDO
        if (fromWallet.getBalance().compareTo(request.amount()) < 0) {
            log.error("Saldo insuficiente | walletId={} | saldo={} | tentativa={}",
                    fromWallet.getId(), fromWallet.getBalance(), request.amount());
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

        log.info("Transação criada | endToEndId={} | de={} | para={} | valor={}",
                endToEndId, fromWallet.getId(), toWallet.getId(), request.amount());

        // DEBITAR
        fromWallet.setBalance(fromWallet.getBalance().subtract(request.amount()));
        walletRepository.save(fromWallet);

        log.info("Saldo atualizado | walletId={} | novoSaldo={}",
                fromWallet.getId(), fromWallet.getBalance());

        var response = new PixTransferResponse(endToEndId, TransactionStatus.PENDING);

        try {
            var serialized = objectMapper.writeValueAsString(response);
            idempotencyRepository.save(new IdempotencyKey(fromWallet.getId(), idempotencyKey, serialized));
            log.info("Saldo atualizado | walletId={} | novoSaldo={}",
                    fromWallet.getId(), fromWallet.getBalance());
        } catch (JsonProcessingException e) {
            log.error("Erro ao salvar idempotent response: {}", e.getMessage());
            throw new RuntimeException("Error saving idempotent response", e);
        }
        return response;
    }
}
