package com.pixservice.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pixservice.domain.enums.PixKeyType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class PixKeyResponse {

    private Long id;
    private Long walletId;
    private PixKeyType keyType;
    private String keyValue;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Sao_Paulo")
    private Instant createdAt;
}
