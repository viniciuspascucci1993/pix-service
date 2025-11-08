package com.pixservice.application.wallet;

import com.pixservice.application.dto.CreateWalletRequest;
import com.pixservice.application.dto.WalletResponse;
import com.pixservice.domain.Wallet;
import com.pixservice.infrastructure.persistence.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateWalletUseCase {

    private final WalletRepository walletRepository;

    public CreateWalletUseCase(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public WalletResponse execute(CreateWalletRequest request) {
        Wallet wallet = new Wallet();
        wallet.setOwnerName(request.ownerName());
        Wallet saved = walletRepository.save(wallet);

        return new WalletResponse(
                saved.getId(),
                saved.getOwnerName(),
                saved.getBalance(),
                saved.getCreatedAt()
        );
    }
}
