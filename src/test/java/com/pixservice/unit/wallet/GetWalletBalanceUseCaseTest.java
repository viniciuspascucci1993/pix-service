package com.pixservice.unit.wallet;

import com.pixservice.application.dto.WalletBalanceResponse;
import com.pixservice.application.wallet.GetWalletBalanceUseCase;
import com.pixservice.domain.Wallet;
import com.pixservice.infrastructure.persistence.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GetWalletBalanceUseCaseTest {

    private WalletRepository walletRepository;
    private GetWalletBalanceUseCase getWalletBalanceUseCase;

    @BeforeEach
    void setup() {
        walletRepository = mock(WalletRepository.class);
        getWalletBalanceUseCase = new GetWalletBalanceUseCase(walletRepository);
    }


    @Test
    void deveRetornarBalaneComSuesso() {
        Long walletId = 1L;
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(BigDecimal.valueOf(250.00));

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        WalletBalanceResponse response = getWalletBalanceUseCase.execute(walletId);

        assertNotNull(response);
        assertEquals(walletId, response.getWalletId());
        assertEquals(BigDecimal.valueOf(250.00), response.getBalance());
        verify(walletRepository).findById(walletId);
    }

    @Test
    void deveLanarExcpetionQuandoWalletNaoEncontrada() {
        Long walletId = 99L;
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> getWalletBalanceUseCase.execute(walletId));

        verify(walletRepository).findById(walletId);
    }
}
