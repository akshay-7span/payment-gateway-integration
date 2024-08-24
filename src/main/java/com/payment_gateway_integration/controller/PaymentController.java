package com.payment_gateway_integration.controller;

import com.payment_gateway_integration.dto.PaymentDTO;
import com.payment_gateway_integration.dto.request.PaymentExecutionRequestDTO;
import com.payment_gateway_integration.dto.request.PaymentRequestDTO;
import com.payment_gateway_integration.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        try {
            PaymentDTO paymentDTO = paymentService.processPayment(paymentRequestDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payment created successfully");
            response.put("payment", paymentDTO);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to process payment: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executePayment(@RequestBody PaymentExecutionRequestDTO paymentExecutionRequestDTO) {
        try {
            PaymentDTO paymentDTO = paymentService.executePayment(paymentExecutionRequestDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("payment", paymentDTO);
            response.put("message", "Payment executed successfully");

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to execute payment: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
