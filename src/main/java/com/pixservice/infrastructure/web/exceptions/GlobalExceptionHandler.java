package com.pixservice.infrastructure.web.exceptions;

import com.pixservice.domain.exceptions.ApiErrorResponse;
import com.pixservice.domain.exceptions.NoTransactionHistoryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoTransactionHistoryException.class)
    public ResponseEntity<ApiErrorResponse> handleNoTransactionHistory(NoTransactionHistoryException ex, WebRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "No Transaction History",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

    }
}
