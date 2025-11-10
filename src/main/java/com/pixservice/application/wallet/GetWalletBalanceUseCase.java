package com.pixservice.application.wallet;

import com.pixservice.application.dto.WalletBalanceResponse;
import com.pixservice.infrastructure.persistence.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GetWalletBalanceUseCase {

    private final WalletRepository walletRepository;

    public GetWalletBalanceUseCase(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public WalletBalanceResponse execute(Long walletId) {

        log.info("Consultando saldo da arteira | walletId={}", walletId);

        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    log.error("Carteira não encontrada | walletId={}", walletId);
                    return new IllegalArgumentException("Wallet not found with ID: " + walletId);
                });

        log.info("Saldo consultado com sucesso | walletId={} | saldoAtual={}", wallet.getId(), wallet.getBalance());

        return new WalletBalanceResponse(wallet.getId(), wallet.getBalance());
    }
}
