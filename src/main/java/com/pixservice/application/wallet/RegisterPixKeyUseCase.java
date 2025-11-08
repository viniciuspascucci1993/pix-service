package com.pixservice.application.wallet;

import com.pixservice.application.dto.PixKeyResponse;
import com.pixservice.application.dto.RegisterPixKeyRequest;
import com.pixservice.domain.PixKey;
import com.pixservice.domain.Wallet;
import com.pixservice.infrastructure.persistence.PixKeyRepository;
import com.pixservice.infrastructure.persistence.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;

@Service
public class RegisterPixKeyUseCase {

    private final WalletRepository walletRepository;
    private final PixKeyRepository pixKeyRepository;

    public RegisterPixKeyUseCase(WalletRepository walletRepository, PixKeyRepository pixKeyRepository) {
        this.walletRepository = walletRepository;
        this.pixKeyRepository = pixKeyRepository;
    }

    @Transactional
    public PixKeyResponse excute(Long walletId, RegisterPixKeyRequest request) {
        // Verifica se a carteira existe
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found with ID: " + walletId));

        // Criar Nova Chave Pix
        PixKey pixKey = new PixKey();
        pixKey.setWallet(wallet);
        pixKey.setKeyType(request.getKeyType());
        pixKey.setKeyValue(request.getKeyValue());
        pixKey.setCreatedAt(Instant.now());

        // salva no repositorio
        PixKey saved = pixKeyRepository.save(pixKey);

        return new PixKeyResponse(
                saved.getId(),
                saved.getWallet().getId(),
                saved.getKeyType(),
                saved.getKeyValue(),
                saved.getCreatedAt()
        );
    }
}
