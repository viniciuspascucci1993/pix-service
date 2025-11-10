package com.pixservice.infrastructure.web;

import com.pixservice.application.dto.PixKeyResponse;
import com.pixservice.application.dto.RegisterPixKeyRequest;
import com.pixservice.application.wallet.RegisterPixKeyUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Wallet-PIX", description = "Registros de chave PIX")
@RestController
@RequestMapping(value = "/wallets/{walletId}/pix-keys")
public class WalletPixKeyController {

    private final RegisterPixKeyUseCase registerPixKeyUseCase;

    public WalletPixKeyController(RegisterPixKeyUseCase registerPixKeyUseCase) {
        this.registerPixKeyUseCase = registerPixKeyUseCase;
    }

    @PostMapping
    public ResponseEntity<PixKeyResponse> registerPixKey(
            @PathVariable("walletId") Long walletId,
            @RequestBody RegisterPixKeyRequest request
    ) {
        PixKeyResponse pixKeyResponse = registerPixKeyUseCase.excute(walletId, request);
        return ResponseEntity.ok(pixKeyResponse);
    }
}
