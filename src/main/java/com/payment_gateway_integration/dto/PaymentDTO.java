package com.payment_gateway_integration.dto;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
public class PaymentDTO {
    private Long id;
    private double amount;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private String status;
    private String approvalUrl;
    private String paymentId;
}
