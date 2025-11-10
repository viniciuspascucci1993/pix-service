package com.pixservice.infrastructure.web;

import com.pixservice.application.wallet.PixWebhookUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@Tag(name = "PIX-WEBHOOK", description = "Operações de PIX-WEBHOOK")
@RestController
@RequestMapping(value = "/pix/webhook")
public class PixWebhookController {

    private final PixWebhookUseCase pixWebhookUseCase;

    public PixWebhookController(PixWebhookUseCase pixWebhookUseCase) {
        this.pixWebhookUseCase = pixWebhookUseCase;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> handleWebhook(@RequestBody Map<String, Object> payload) {
        String endToEndId = (String) payload.get("endToEndId");
        String eventId = (String) payload.get("eventId");
        String eventType = (String) payload.get("eventType");
        Instant occurredAt = Instant.parse((String) payload.get("occurredAt"));

        pixWebhookUseCase.execute(endToEndId, eventId, eventType, occurredAt);

        return ResponseEntity.ok(Map.of(
                "endToEndId", endToEndId,
                "eventId", eventId,
                "status", "processed"
        ));
    }
}
