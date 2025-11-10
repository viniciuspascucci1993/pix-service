package com.pixservice.infrastructure.web;

import com.pixservice.application.dto.PixTransferRequest;
import com.pixservice.application.dto.PixTransferResponse;
import com.pixservice.application.wallet.PixTransferUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "PIX", description = "Operações de PIX")
@RestController
@RequestMapping(value = "/pix")
public class PixController {
    private final PixTransferUseCase pixTransferUseCase;

    public PixController(PixTransferUseCase pixTransferUseCase) {
        this.pixTransferUseCase = pixTransferUseCase;
    }

    @PostMapping("/transfers")
    public ResponseEntity<PixTransferResponse> transfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody PixTransferRequest request) {

        PixTransferResponse response = pixTransferUseCase.execute(idempotencyKey, request);
        return ResponseEntity.ok(response);
    }
}
