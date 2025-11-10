package com.pixservice.unit.wallet;

import com.pixservice.application.dto.WithdrawRequest;
import com.pixservice.application.dto.WithdrawResponse;
import com.pixservice.application.wallet.WithdrawFromWalletUseCase;
import com.pixservice.domain.Transaction;
import com.pixservice.domain.Wallet;
import com.pixservice.infrastructure.persistence.TransactionRepository;
import com.pixservice.infrastructure.persistence.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WithdrawFromWalletUseCaseTest {

    private WalletRepository walletRepository;
    private TransactionRepository transactionRepository;

    private WithdrawFromWalletUseCase withdrawFromWalletUseCase;

    @BeforeEach
    void setup() {
        walletRepository = mock(WalletRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        withdrawFromWalletUseCase = new WithdrawFromWalletUseCase(walletRepository, transactionRepository);
    }

    @Test
    void deveRealizarSaqueComSucesso() {
        Long walletId = 1L;
        BigDecimal saldoInicial = BigDecimal.valueOf(1000);
        BigDecimal valorSaque = BigDecimal.valueOf(200);

        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(saldoInicial);

        WithdrawRequest request = new WithdrawRequest(valorSaque);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        WithdrawResponse response = withdrawFromWalletUseCase.execute(walletId, request);

        assertNotNull(response);
        assertEquals(walletId, response.walletId());
        assertEquals(BigDecimal.valueOf(800), response.newBalance()); // saldo atualizado
        assertEquals(valorSaque, response.amount());
        assertNotNull(response.timestamp());

        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void deveLancarExcecaoQuandoCarteiraNaoEncontrada() {
        Long walletId = 99L;
        WithdrawRequest request = new WithdrawRequest(BigDecimal.valueOf(100));

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                withdrawFromWalletUseCase.execute(walletId, request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoSaldoInsuficiente() {
        Long walletId = 2L;
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(BigDecimal.valueOf(50)); // saldo menor que o saque

        WithdrawRequest request = new WithdrawRequest(BigDecimal.valueOf(100));

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(IllegalArgumentException.class, () ->
                withdrawFromWalletUseCase.execute(walletId, request));

        verify(walletRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }


}
