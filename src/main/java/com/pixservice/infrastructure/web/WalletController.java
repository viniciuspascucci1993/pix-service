package com.pixservice.infrastructure.web;

import com.pixservice.application.dto.CreateWalletRequest;
import com.pixservice.application.dto.WalletBalanceResponse;
import com.pixservice.application.dto.WalletResponse;
import com.pixservice.application.wallet.CreateWalletUseCase;
import com.pixservice.application.wallet.GetWalletBalanceUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/wallets")
public class WalletController {

    private final CreateWalletUseCase createWalletUseCase;
    private final GetWalletBalanceUseCase getWalletBalanceUseCase;

    public WalletController(CreateWalletUseCase createWalletUseCase, GetWalletBalanceUseCase getWalletBalanceUseCase) {
        this.createWalletUseCase = createWalletUseCase;
        this.getWalletBalanceUseCase = getWalletBalanceUseCase;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        WalletResponse walletResponse = createWalletUseCase.execute(request);
        return ResponseEntity.ok(walletResponse);
    }

    @GetMapping("/{walletId}/balance")
    public ResponseEntity<WalletBalanceResponse> getBalance(@PathVariable("walletId") Long walletId) {
        WalletBalanceResponse walletBalanceResponse = getWalletBalanceUseCase.execute(walletId);
        return ResponseEntity.ok(walletBalanceResponse);
    }
}
