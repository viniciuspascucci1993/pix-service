package com.pixservice.unit.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pixservice.application.dto.PixTransferRequest;
import com.pixservice.application.dto.PixTransferResponse;
import com.pixservice.application.wallet.PixTransferUseCase;
import com.pixservice.domain.IdempotencyKey;
import com.pixservice.domain.PixKey;
import com.pixservice.domain.Wallet;
import com.pixservice.domain.enums.TransactionStatus;
import com.pixservice.infrastructure.persistence.IdempotencyRepository;
import com.pixservice.infrastructure.persistence.PixKeyRepository;
import com.pixservice.infrastructure.persistence.TransactionRepository;
import com.pixservice.infrastructure.persistence.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PixTransferUseCaseTest {

    private WalletRepository walletRepository;
    private PixKeyRepository pixKeyRepository;
    private TransactionRepository transactionRepository;
    private IdempotencyRepository idempotencyRepository;

    private PixTransferUseCase pixTransferUseCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        walletRepository = mock(WalletRepository.class);
        pixKeyRepository = mock(PixKeyRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        idempotencyRepository = mock(IdempotencyRepository.class);
        objectMapper = new ObjectMapper();

        pixTransferUseCase = new PixTransferUseCase(
                walletRepository,
                pixKeyRepository,
                transactionRepository,
                idempotencyRepository,
                objectMapper
        );
    }

    @Test
    void deveRealizarTransferenciaComSucesso() {
        String idemKey = "1234";
        Long walletId = 1L;

        Wallet fromWallet = new Wallet();
        fromWallet.setId(walletId);
        fromWallet.setBalance(BigDecimal.valueOf(1000));

        Wallet toWallet = new Wallet();
        toWallet.setId(2L);

        PixKey toPixKey = new PixKey();
        toPixKey.setWallet(toWallet);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(fromWallet));
        when(pixKeyRepository.findByKeyValue("pix@destino.com")).thenReturn(Optional.of(toPixKey));
        when(idempotencyRepository.findByWalletIdAndKeyValue(walletId, idemKey)).thenReturn(Optional.empty());

        PixTransferRequest request = new PixTransferRequest(walletId, "pix@destino.com", BigDecimal.valueOf(100));

        // when
        PixTransferResponse response = pixTransferUseCase.execute(idemKey, request);

        // then
        assertNotNull(response);
        assertEquals(TransactionStatus.PENDING, response.status());
        assertTrue(response.endToEndId().startsWith("E2E-"));

        verify(transactionRepository).save(any());
        verify(walletRepository, times(1)).save(any(Wallet.class));
        verify(idempotencyRepository).save(any(IdempotencyKey.class));
    }

    @Test
    void deveRetornarRespostaDeIdempotenciaQuandoJaExistente() throws Exception {
        String idemKey = "IDEMP-001";
        Long walletId = 1L;

        Wallet fromWallet = new Wallet();
        fromWallet.setId(walletId);

        PixTransferResponse storedResponse = new PixTransferResponse("E2E-999", TransactionStatus.PENDING);
        String json = new ObjectMapper().writeValueAsString(storedResponse);
        IdempotencyKey key = new IdempotencyKey(walletId, idemKey, json);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(fromWallet));
        when(pixKeyRepository.findByKeyValue(anyString())).thenReturn(Optional.of(new PixKey()));
        when(idempotencyRepository.findByWalletIdAndKeyValue(walletId, idemKey)).thenReturn(Optional.of(key));

        PixTransferRequest request = new PixTransferRequest(walletId, "pix@teste.com", BigDecimal.valueOf(50));

        PixTransferResponse response = pixTransferUseCase.execute(idemKey, request);

        assertEquals(storedResponse.endToEndId(), response.endToEndId());
        assertEquals(storedResponse.status(), response.status());

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void deveLancarErroQuandoSaldoInsuficiente() {
        Long walletId = 1L;

        Wallet fromWallet = new Wallet();
        fromWallet.setId(walletId);
        fromWallet.setBalance(BigDecimal.valueOf(10));

        Wallet toWallet = new Wallet();
        toWallet.setId(2L);
        PixKey toPixKey = new PixKey();
        toPixKey.setWallet(toWallet);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(fromWallet));
        when(pixKeyRepository.findByKeyValue(anyString())).thenReturn(Optional.of(toPixKey));
        when(idempotencyRepository.findByWalletIdAndKeyValue(any(), any())).thenReturn(Optional.empty());

        PixTransferRequest request = new PixTransferRequest(walletId, "pix@destino.com", BigDecimal.valueOf(100));

        assertThrows(IllegalArgumentException.class, () -> pixTransferUseCase.execute("key-001", request));

        verify(transactionRepository, never()).save(any());
        verify(idempotencyRepository, never()).save(any());
    }

}
