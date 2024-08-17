package com.payment_gateway_integration.exception;

import com.payment_gateway_integration.dto.response.ErrorResponseDTO;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<ErrorResponseDTO> handleStripeException(StripeException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomStripeException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufficientBalanceException(CustomStripeException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ex.getMessage(), HttpStatus.PAYMENT_REQUIRED.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.PAYMENT_REQUIRED);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}


