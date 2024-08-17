package com.payment_gateway_integration.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {
    private String message;
    private int status;
}
