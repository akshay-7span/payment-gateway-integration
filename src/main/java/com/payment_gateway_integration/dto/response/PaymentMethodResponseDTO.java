package com.payment_gateway_integration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentMethodResponseDTO {
    private String id;
    private String type;
    private String customer;
}
