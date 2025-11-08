package com.pixservice.infrastructure.web;

import com.pixservice.application.dto.CreateWalletRequest;
import com.pixservice.application.dto.WalletBalanceHistoricalResponse;
import com.pixservice.application.dto.WalletBalanceResponse;
import com.pixservice.application.dto.WalletResponse;
import com.pixservice.application.wallet.CreateWalletUseCase;
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

    public WalletController(CreateWalletUseCase createWalletUseCase,
                            GetWalletBalanceUseCase getWalletBalanceUseCase,
                            GetWalletHistoricalBalanceUseCase getWalletHistoricalBalanceUseCase) {
        this.createWalletUseCase = createWalletUseCase;
        this.getWalletBalanceUseCase = getWalletBalanceUseCase;
        this.getWalletHistoricalBalanceUseCase = getWalletHistoricalBalanceUseCase;
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
}
