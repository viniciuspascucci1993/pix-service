package com.pixservice.infrastructure.web;

import com.pixservice.application.dto.PixKeyResponse;
import com.pixservice.application.dto.RegisterPixKeyRequest;
import com.pixservice.application.wallet.RegisterPixKeyUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/wallets/{walletId}/pix-keys")
public class PixController {

    private final RegisterPixKeyUseCase registerPixKeyUseCase;

    public PixController(RegisterPixKeyUseCase registerPixKeyUseCase) {
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
