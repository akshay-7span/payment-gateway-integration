package com.payment_gateway_integration.exception;

import lombok.Getter;

@Getter
public class PayPalCustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public PayPalCustomException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public enum ErrorCode {
        INSUFFICIENT_BALANCE,
        CUSTOMER_NOT_FOUND,
        PAYPAL_PAYMENT_FAIL,
        PAYPAL_BALANCE_FETCH_FAIL,
        PAYPAL_PAYMENT_DUPLICATE,
        PAYMENT_ALREADY_DONE
    }
}
