package com.pixservice.application.wallet;

import com.pixservice.domain.IdempotencyKey;
import com.pixservice.domain.Transaction;
import com.pixservice.domain.enums.TransactionStatus;
import com.pixservice.infrastructure.persistence.IdempotencyRepository;
import com.pixservice.infrastructure.persistence.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
public class PixWebhookUseCase {

    private final TransactionRepository transactionRepository;
    private final IdempotencyRepository idempotencyRepository;

    public PixWebhookUseCase(TransactionRepository transactionRepository, IdempotencyRepository idempotencyRepository) {
        this.transactionRepository = transactionRepository;
        this.idempotencyRepository = idempotencyRepository;
    }

    @Transactional
    public void execute(String endToEndId, String eventId, String eventType, Instant occurredAt) {

        log.info("Recebido webhook Pix | endToEndId={} | eventId={} | tipoEvento={} | ocorridoEm={}",
                endToEndId, eventId, eventType, occurredAt);

        // busca a transação
        Transaction transaction = transactionRepository.findByEndToEndId(endToEndId)
                .orElseThrow(() -> {
                    log.error("Transação não encontrada para endToEndId={}", endToEndId);
                    return new RuntimeException("Transaction not found for endToEndId: " + endToEndId);
                });

        Long walletId = transaction.getFromWallet().getId();

        // Idempotência: se já existe, não preciso processar novamente
        Optional<IdempotencyKey> existing = idempotencyRepository.findByWalletIdAndKeyValue(walletId, eventId);
        if (existing.isPresent()) {
            log.warn(" Webhook já processado anteriormente | walletId={} | eventId={}", walletId, eventId);
            return;
        }

        // Atualizamos os status com base no tipo do evento realizado
        if ("CONFIRMED".equalsIgnoreCase(eventType)) {
            transaction.setStatus(TransactionStatus.CONFIRMED);
            log.info("Transação confirmada | endToEndId={} | walletId={}", endToEndId, walletId);

        } else if ("FAILED".equalsIgnoreCase(eventType)) {
            transaction.setStatus(TransactionStatus.REJECTED);
            log.info("Transação rejeitada | endToEndId={} | walletId={}", endToEndId, walletId);

        } else {
            log.warn("Tipo de evento desconhecido recebido | eventType={} | endToEndId={}", eventType, endToEndId);
        }

        transactionRepository.save(transaction);

        // Registrar a Idempotencia do evento
        IdempotencyKey key = new IdempotencyKey();
        key.setKeyValue(eventId);
        key.setResponseBody("Webhook processed for " + endToEndId);
        key.setCreatedAt(occurredAt);
        idempotencyRepository.save(key);

        log.info("Webhook processado e persistido com sucesso | eventId={} | walletId={}", eventId, walletId);
    }
}
