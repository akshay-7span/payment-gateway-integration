package com.payment_gateway_integration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PayPalCustomException.class)
    public ResponseEntity<String> handlePayPalCustomException(PayPalCustomException ex) {
        return switch (ex.getErrorCode()) {
            case INSUFFICIENT_BALANCE, PAYPAL_PAYMENT_DUPLICATE, PAYMENT_ALREADY_DONE,PAYPAL_BALANCE_FETCH_FAIL ->
                    new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
            case CUSTOMER_NOT_FOUND -> new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
            case PAYPAL_PAYMENT_FAIL -> new ResponseEntity<>(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
            default ->
                    new ResponseEntity<>("An unexpected PayPal error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
