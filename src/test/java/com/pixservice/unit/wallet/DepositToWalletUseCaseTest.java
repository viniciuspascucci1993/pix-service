package com.pixservice.unit.wallet;

import com.pixservice.application.dto.DepositRequest;
import com.pixservice.application.dto.DepositResponse;
import com.pixservice.application.wallet.DepositToWalletUseCase;
import com.pixservice.domain.Transaction;
import com.pixservice.domain.Wallet;
import com.pixservice.infrastructure.persistence.TransactionRepository;
import com.pixservice.infrastructure.persistence.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DepositToWalletUseCaseTest {

    private WalletRepository walletRepository;
    private TransactionRepository transactionRepository;

    private DepositToWalletUseCase depositToWalletUseCase;

    @BeforeEach
    void setup() {
        walletRepository = mock(WalletRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        depositToWalletUseCase = new DepositToWalletUseCase(walletRepository, transactionRepository);
    }

    @Test
    void deveRealizarDepositoComSucesso() {
        Long walletId = 1L;
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(BigDecimal.valueOf(100));

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        DepositRequest request = new DepositRequest(BigDecimal.valueOf(50));

        DepositResponse response = depositToWalletUseCase.execute(walletId, request);

        assertNotNull(response);
        assertEquals(walletId, response.walletId());
        assertEquals(BigDecimal.valueOf(50), response.amount());
        assertEquals("CONFIRMED", response.status());

        verify(walletRepository).save(wallet);
        assertEquals(BigDecimal.valueOf(150), wallet.getBalance());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        assertEquals(BigDecimal.valueOf(50), captor.getValue().getAmount());
    }

    @Test
    void shouldThrowExceptionWhenWalletNotFound() {
        // given
        when(walletRepository.findById(anyLong())).thenReturn(Optional.empty());
        DepositRequest request = new DepositRequest(BigDecimal.valueOf(100));

        // then
        assertThrows(IllegalArgumentException.class,
                () -> depositToWalletUseCase.execute(99L, request));
    }

}
