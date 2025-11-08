package com.pixservice.application.wallet;

import com.pixservice.application.dto.WalletBalanceResponse;
import com.pixservice.infrastructure.persistence.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class GetWalletBalanceUseCase {

    private final WalletRepository walletRepository;

    public GetWalletBalanceUseCase(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public WalletBalanceResponse execute(Long walletId) {
        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with ID: " + walletId));

        return new WalletBalanceResponse(wallet.getId(), wallet.getBalance());
    }
}
