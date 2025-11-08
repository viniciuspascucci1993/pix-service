package com.pixservice.domain.exceptions;

public class NoTransactionHistoryException extends RuntimeException {

    public NoTransactionHistoryException(Long walletId, String at) {
        super(String.format("No transaction history found for wallet ID %d at %s", walletId, at));
    }
}
