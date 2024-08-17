package com.payment_gateway_integration.dto.request;

import lombok.Data;

@Data
public class BalanceRequestDTO {
    private Long topUpAmount;
    private String paymentMethodId;
}


