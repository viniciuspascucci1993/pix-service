package com.pixservice.unit.wallet;

import com.pixservice.application.dto.CreateWalletRequest;
import com.pixservice.application.dto.WalletResponse;
import com.pixservice.application.wallet.CreateWalletUseCase;
import com.pixservice.domain.Wallet;
import com.pixservice.infrastructure.persistence.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateWalletUseCaseTest {

    private WalletRepository walletRepository;
    private CreateWalletUseCase createWalletUseCase;

    @BeforeEach
    void setup() {
        walletRepository = mock(WalletRepository.class);
        createWalletUseCase = new CreateWalletUseCase(walletRepository);
    }

    @Test
    void deveCriarCarteiraComSucesso() {
        CreateWalletRequest request = new CreateWalletRequest("Vinícius");
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setOwnerName("Vinícius");
        wallet.setBalance(BigDecimal.ZERO);

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        WalletResponse response = createWalletUseCase.execute(request);

        assertNotNull(response);
        assertEquals("Vinícius", response.ownerName());
        assertEquals(BigDecimal.ZERO, response.balance());

        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(captor.capture());
        assertEquals("Vinícius", captor.getValue().getOwnerName());
    }

    @Test
    void deveLancarExceptionWhenNomeCarteiraNull() {
        CreateWalletRequest request = new CreateWalletRequest(null);
        assertThrows(NullPointerException.class, () -> createWalletUseCase.execute(request));
    }

}
