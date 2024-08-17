package com.payment_gateway_integration.dto.request;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private String stripeCustomerId;
    private Double amount;
    private String paymentMethodId;
}

