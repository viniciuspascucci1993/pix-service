package com.pixservice.unit.wallet;

import com.pixservice.application.dto.WalletBalanceHistoricalResponse;
import com.pixservice.application.wallet.GetWalletHistoricalBalanceUseCase;
import com.pixservice.domain.Wallet;
import com.pixservice.domain.exceptions.NoTransactionHistoryException;
import com.pixservice.infrastructure.persistence.TransactionRepository;
import com.pixservice.infrastructure.persistence.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class GetWalletHistoricalBalanceUseCaseTest {

    private WalletRepository walletRepository;
    private TransactionRepository transactionRepository;

    private GetWalletHistoricalBalanceUseCase getWalletHistoricalBalanceUseCase;

    @BeforeEach
    void setup() {
        walletRepository = mock(WalletRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        getWalletHistoricalBalanceUseCase = new GetWalletHistoricalBalanceUseCase(walletRepository, transactionRepository);
    }

    @Test
    void deveRetornarBalancoHistoricoComSucesso() {
        Long walletId = 1L;
        Instant date = Instant.parse("2025-11-08T12:00:00Z");
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(BigDecimal.valueOf(500));

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.sumBalanceUntil(eq(walletId), any()))
                .thenReturn(Optional.of(BigDecimal.valueOf(300)));

        WalletBalanceHistoricalResponse response = getWalletHistoricalBalanceUseCase.execute(walletId, date);

        assertNotNull(response);
        assertEquals(walletId, response.getWalletId());
        assertEquals(BigDecimal.valueOf(300), response.getBalance());
        assertTrue(response.getAt().isAfter(date));
    }

    @Test
    void deveLancarExceptionQuandoCarteiraNaoEncontrada() {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> getWalletHistoricalBalanceUseCase.execute(1L, Instant.now()));

        verify(transactionRepository, never()).sumBalanceUntil(anyLong(), any());
    }

    @Test
    void deveLancarWxceptionQuandoNaoHaTransacao() {
        Long walletId = 1L;
        Instant date = Instant.now();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.sumBalanceUntil(eq(walletId), any()))
                .thenReturn(Optional.of(BigDecimal.ZERO));

        assertThrows(NoTransactionHistoryException.class,
                () -> getWalletHistoricalBalanceUseCase.execute(walletId, date));
    }
}
