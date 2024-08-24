package com.payment_gateway_integration.dto.request;

import lombok.Data;

@Data
public class TransferRequestDTO {
    private Long fromCustomerId;
    private Long toCustomerId;
    private double amount;
}
