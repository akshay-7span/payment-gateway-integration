package com.payment_gateway_integration.dto.response;

import com.payment_gateway_integration.dto.PaymentDTO;
import lombok.Data;
import java.util.List;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String email;
    private double balance;
}
