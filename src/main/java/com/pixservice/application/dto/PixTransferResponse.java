package com.pixservice.application.dto;

import com.pixservice.domain.enums.TransactionStatus;

public record PixTransferResponse(
        String endToEndId,
        TransactionStatus status
) {
}
