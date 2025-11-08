package com.pixservice.infrastructure.web;

import com.pixservice.application.dto.CreateWalletRequest;
import com.pixservice.application.dto.WalletResponse;
import com.pixservice.application.wallet.CreateWalletUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/wallets")
public class WalletController {

    private final CreateWalletUseCase createWalletUseCase;

    public WalletController(CreateWalletUseCase createWalletUseCase) {
        this.createWalletUseCase = createWalletUseCase;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        WalletResponse walletResponse = createWalletUseCase.execute(request);
        return ResponseEntity.ok(walletResponse);
    }
}
