package com.pixservice.infrastructure.web;

import com.pixservice.application.dto.*;
import com.pixservice.application.wallet.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Tag(name = "Carteiras", description = "Gerenciamento de carteiras e saldos")
@RestController
@RequestMapping(value = "/wallets")
public class WalletController {

    private final CreateWalletUseCase createWalletUseCase;
    private final GetWalletBalanceUseCase getWalletBalanceUseCase;
    private final GetWalletHistoricalBalanceUseCase getWalletHistoricalBalanceUseCase;
    private final DepositToWalletUseCase depositToWalletUseCase;
    private final WithdrawFromWalletUseCase withdrawFromWalletUseCase;

    public WalletController(CreateWalletUseCase createWalletUseCase,
                            GetWalletBalanceUseCase getWalletBalanceUseCase,
                            GetWalletHistoricalBalanceUseCase getWalletHistoricalBalanceUseCase,
                            DepositToWalletUseCase depositToWalletUseCase,
                            WithdrawFromWalletUseCase withdrawFromWalletUseCase) {
        this.createWalletUseCase = createWalletUseCase;
        this.getWalletBalanceUseCase = getWalletBalanceUseCase;
        this.getWalletHistoricalBalanceUseCase = getWalletHistoricalBalanceUseCase;
        this.depositToWalletUseCase = depositToWalletUseCase;
        this.withdrawFromWalletUseCase = withdrawFromWalletUseCase;
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

    @PostMapping("/{walletId}/withdraw")
    public ResponseEntity<WithdrawResponse> withdraw(
            @PathVariable("walletId") Long walletId,
            @RequestBody WithdrawRequest request
    ) {
        WithdrawResponse withdrawResponse =  withdrawFromWalletUseCase.execute(walletId, request);
        return ResponseEntity.ok(withdrawResponse);
    }

}
