package com.payment_gateway_integration.dto.request;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Long customerId;
    private double amount;
}
