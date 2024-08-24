package com.payment_gateway_integration.service;

import com.payment_gateway_integration.dto.PaymentDTO;
import com.payment_gateway_integration.dto.request.PaymentExecutionRequestDTO;
import com.payment_gateway_integration.dto.request.PaymentRequestDTO;

public interface PaymentService {
    PaymentDTO processPayment(PaymentRequestDTO paymentRequestDTO);

    public PaymentDTO executePayment(PaymentExecutionRequestDTO paymentExecutionRequestDTO);
}
