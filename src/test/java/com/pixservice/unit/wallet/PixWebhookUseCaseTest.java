package com.pixservice.unit.wallet;

import com.pixservice.application.wallet.PixWebhookUseCase;
import com.pixservice.domain.IdempotencyKey;
import com.pixservice.domain.Transaction;
import com.pixservice.domain.Wallet;
import com.pixservice.domain.enums.TransactionStatus;
import com.pixservice.infrastructure.persistence.IdempotencyRepository;
import com.pixservice.infrastructure.persistence.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PixWebhookUseCaseTest {

    private TransactionRepository transactionRepository;
    private IdempotencyRepository idempotencyRepository;

    private PixWebhookUseCase pixWebhookUseCase;

    @BeforeEach
    void setup() {
        transactionRepository = mock(TransactionRepository.class);
        idempotencyRepository = mock(IdempotencyRepository.class);
        pixWebhookUseCase = new PixWebhookUseCase(transactionRepository, idempotencyRepository);
    }

    @Test
    void deveProcessarWebhookConfirmadoComSucesso() {
        String endToEndId = "E2E-123";
        String eventId = "EVT-001";
        String eventType = "CONFIRMED";
        Instant occurredAt = Instant.now();

        Wallet wallet = new Wallet();
        wallet.setId(1L);

        Transaction transaction = new Transaction();
        transaction.setEndToEndId(endToEndId);
        transaction.setFromWallet(wallet);
        transaction.setStatus(TransactionStatus.PENDING);

        when(transactionRepository.findByEndToEndId(endToEndId)).thenReturn(Optional.of(transaction));
        when(idempotencyRepository.findByWalletIdAndKeyValue(wallet.getId(), eventId))
                .thenReturn(Optional.empty());

        pixWebhookUseCase.execute(endToEndId, eventId, eventType, occurredAt);

        assertEquals(TransactionStatus.CONFIRMED, transaction.getStatus());
        verify(transactionRepository).save(transaction);
        verify(idempotencyRepository).save(any(IdempotencyKey.class));
    }

    @Test
    void deveProcessarWebhookFalhadoComSucesso() {
        String endToEndId = "E2E-456";
        String eventId = "EVT-002";
        String eventType = "FAILED";
        Instant occurredAt = Instant.now();

        Wallet wallet = new Wallet();
        wallet.setId(2L);

        Transaction transaction = new Transaction();
        transaction.setEndToEndId(endToEndId);
        transaction.setFromWallet(wallet);
        transaction.setStatus(TransactionStatus.PENDING);

        when(transactionRepository.findByEndToEndId(endToEndId)).thenReturn(Optional.of(transaction));
        when(idempotencyRepository.findByWalletIdAndKeyValue(wallet.getId(), eventId))
                .thenReturn(Optional.empty());

        pixWebhookUseCase.execute(endToEndId, eventId, eventType, occurredAt);

        assertEquals(TransactionStatus.REJECTED, transaction.getStatus());
        verify(transactionRepository).save(transaction);
        verify(idempotencyRepository).save(any(IdempotencyKey.class));
    }

    @Test
    void deveIgnorarEventoJaProcessadoPorIdempotencia() {
        String endToEndId = "E2E-789";
        String eventId = "EVT-003";
        String eventType = "CONFIRMED";
        Instant occurredAt = Instant.now();

        Wallet wallet = new Wallet();
        wallet.setId(3L);

        Transaction transaction = new Transaction();
        transaction.setEndToEndId(endToEndId);
        transaction.setFromWallet(wallet);

        IdempotencyKey existingKey = new IdempotencyKey();
        existingKey.setKeyValue(eventId);

        when(transactionRepository.findByEndToEndId(endToEndId)).thenReturn(Optional.of(transaction));
        when(idempotencyRepository.findByWalletIdAndKeyValue(wallet.getId(), eventId))
                .thenReturn(Optional.of(existingKey));

        pixWebhookUseCase.execute(endToEndId, eventId, eventType, occurredAt);

        verify(transactionRepository, never()).save(any());
        verify(idempotencyRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoTransacaoNaoEncontrada() {
        String endToEndId = "E2E-999";
        when(transactionRepository.findByEndToEndId(endToEndId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                pixWebhookUseCase.execute(endToEndId, "EVT-004", "CONFIRMED", Instant.now()));

        verify(transactionRepository, never()).save(any());
        verify(idempotencyRepository, never()).save(any());
    }

}
