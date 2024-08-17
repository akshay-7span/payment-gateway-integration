package com.payment_gateway_integration.exception;

public class CustomStripeException extends RuntimeException {

    public CustomStripeException(String message) {
        super(message);
    }
}
