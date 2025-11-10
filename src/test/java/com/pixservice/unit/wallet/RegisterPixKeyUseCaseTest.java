package com.pixservice.unit.wallet;

import com.pixservice.application.dto.PixKeyResponse;
import com.pixservice.application.dto.RegisterPixKeyRequest;
import com.pixservice.application.wallet.RegisterPixKeyUseCase;
import com.pixservice.domain.PixKey;
import com.pixservice.domain.Wallet;
import com.pixservice.domain.enums.PixKeyType;
import com.pixservice.infrastructure.persistence.PixKeyRepository;
import com.pixservice.infrastructure.persistence.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterPixKeyUseCaseTest {

    private WalletRepository walletRepository;
    private PixKeyRepository pixKeyRepository;
    private RegisterPixKeyUseCase registerPixKeyUseCase;

    @BeforeEach
    void setup() {
        walletRepository = mock(WalletRepository.class);
        pixKeyRepository = mock(PixKeyRepository.class);
        registerPixKeyUseCase = new RegisterPixKeyUseCase(walletRepository, pixKeyRepository);
    }

    @Test
    void deveRegistrarChavePixComSucesso() {
        Long walletId = 1L;
        RegisterPixKeyRequest request = new RegisterPixKeyRequest();
        request.setKeyType(PixKeyType.EMAIL);
        request.setKeyValue("user@email.com");

        Wallet wallet = new Wallet();
        wallet.setId(walletId);

        PixKey savedPixKey = new PixKey();
        savedPixKey.setId(10L);
        savedPixKey.setWallet(wallet);
        savedPixKey.setKeyType(PixKeyType.EMAIL);
        savedPixKey.setKeyValue("user@email.com");
        savedPixKey.setCreatedAt(Instant.now());

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(pixKeyRepository.save(any(PixKey.class))).thenReturn(savedPixKey);

        PixKeyResponse response = registerPixKeyUseCase.excute(walletId, request);

        assertNotNull(response);
        assertEquals(savedPixKey.getId(), response.getId());
        assertEquals(walletId, response.getWalletId());
        assertEquals(PixKeyType.EMAIL, response.getKeyType());
        assertEquals("user@email.com", response.getKeyValue());
        assertNotNull(response.getCreatedAt());

        verify(walletRepository).findById(walletId);
        verify(pixKeyRepository).save(any(PixKey.class));
    }

    @Test
    void deveLancarExcecaoQuandoCarteiraNaoExiste() {
        Long walletId = 999L;
        RegisterPixKeyRequest request = new RegisterPixKeyRequest();
        request.setKeyType(PixKeyType.CPF);
        request.setKeyValue("12345678901");

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                registerPixKeyUseCase.excute(walletId, request));

        verify(pixKeyRepository, never()).save(any());
    }

    @Test
    void deveGerarDataDeCriacaoNaChavePix() {
        // given
        Long walletId = 1L;
        RegisterPixKeyRequest request = new RegisterPixKeyRequest();
        request.setKeyType(PixKeyType.EMAIL);
        request.setKeyValue("teste@dominio.com");

        Wallet wallet = new Wallet();
        wallet.setId(walletId);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(pixKeyRepository.save(any(PixKey.class))).thenAnswer(invocation -> {
            PixKey pixKey = invocation.getArgument(0);
            pixKey.setId(15L);
            pixKey.setCreatedAt(Instant.now());
            return pixKey;
        });

        PixKeyResponse response = registerPixKeyUseCase.excute(walletId, request);

        assertNotNull(response.getCreatedAt());
        verify(pixKeyRepository).save(any(PixKey.class));
    }

}
