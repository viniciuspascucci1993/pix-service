package com.pixservice.infrastructure.web;

import com.pixservice.application.dto.*;
import com.pixservice.application.wallet.CreateWalletUseCase;
import com.pixservice.application.wallet.DepositToWalletUseCase;
import com.pixservice.application.wallet.GetWalletBalanceUseCase;
import com.pixservice.application.wallet.GetWalletHistoricalBalanceUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping(value = "/wallets")
public class WalletController {

    private final CreateWalletUseCase createWalletUseCase;
    private final GetWalletBalanceUseCase getWalletBalanceUseCase;
    private final GetWalletHistoricalBalanceUseCase getWalletHistoricalBalanceUseCase;

    private final DepositToWalletUseCase depositToWalletUseCase;

    public WalletController(CreateWalletUseCase createWalletUseCase,
                            GetWalletBalanceUseCase getWalletBalanceUseCase,
                            GetWalletHistoricalBalanceUseCase getWalletHistoricalBalanceUseCase,
                            DepositToWalletUseCase depositToWalletUseCase) {
        this.createWalletUseCase = createWalletUseCase;
        this.getWalletBalanceUseCase = getWalletBalanceUseCase;
        this.getWalletHistoricalBalanceUseCase = getWalletHistoricalBalanceUseCase;
        this.depositToWalletUseCase = depositToWalletUseCase;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        WalletResponse walletResponse = createWalletUseCase.execute(request);
        return ResponseEntity.ok(walletResponse);
    }

    @GetMapping("/{walletId}/balance")
    public ResponseEntity<WalletBalanceResponse> getBalance(
            @PathVariable("walletId") Long walletId) {
        WalletBalanceResponse walletBalanceResponse = getWalletBalanceUseCase.execute(walletId);
        return ResponseEntity.ok(walletBalanceResponse);
    }

    @GetMapping("/{walletId}/balance/at")
    public ResponseEntity<WalletBalanceHistoricalResponse> getBalanceAt(
            @PathVariable("walletId") Long walletId,
            @RequestParam(value = "at", required = false) Instant at
            ) {
        WalletBalanceHistoricalResponse response = getWalletHistoricalBalanceUseCase
                .execute(walletId, at);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<DepositResponse> deposit(
            @PathVariable("walletId") Long walletId,
            @Valid @RequestBody DepositRequest request
    ) {
        DepositResponse depositResponse = depositToWalletUseCase.execute(walletId, request);
        return ResponseEntity.ok(depositResponse);
    }
}
