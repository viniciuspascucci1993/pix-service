package com.pixservice.application.wallet;

import com.pixservice.application.dto.CreateWalletRequest;
import com.pixservice.application.dto.WalletResponse;
import com.pixservice.domain.Wallet;
import com.pixservice.infrastructure.persistence.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CreateWalletUseCase {

    private final WalletRepository walletRepository;

    public CreateWalletUseCase(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public WalletResponse execute(CreateWalletRequest request) {

        log.info("Iniciando criação de nova carteira | ownerName={}", request.ownerName());

        Wallet wallet = new Wallet();
        wallet.setOwnerName(request.ownerName());
        Wallet saved = walletRepository.save(wallet);

        log.info("Carteira criada com sucesso | walletId={} | owner={} | saldoInicial={}",
                saved.getId(), saved.getOwnerName(), saved.getBalance());

        return new WalletResponse(
                saved.getId(),
                saved.getOwnerName(),
                saved.getBalance(),
                saved.getCreatedAt()
        );
    }
}
