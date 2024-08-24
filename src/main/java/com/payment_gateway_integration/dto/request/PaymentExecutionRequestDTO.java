package com.payment_gateway_integration.dto.request;

import lombok.Data;

@Data
public class PaymentExecutionRequestDTO {
    private String paymentId;
    private String payerId;
}
