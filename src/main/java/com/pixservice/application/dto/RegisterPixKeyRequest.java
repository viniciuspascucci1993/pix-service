package com.pixservice.application.dto;

import com.pixservice.domain.enums.PixKeyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterPixKeyRequest {

    private PixKeyType keyType; // tipo da chave
    private String keyValue; // valor da chave
}
