package com.payment_gateway_integration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionResponseDTO {
    private String id;
    private Double amount;
    private String status;
    private Long customerId;
}

