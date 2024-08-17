package com.payment_gateway_integration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class BalanceResponseDTO {
    private Long available;
    private Long pending;

}

