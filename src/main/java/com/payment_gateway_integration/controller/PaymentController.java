package com.payment_gateway_integration.controller;

import com.payment_gateway_integration.dto.request.PaymentRequestDTO;
import com.payment_gateway_integration.dto.response.PaymentMethodResponseDTO;
import com.payment_gateway_integration.dto.response.TransactionResponseDTO;
import com.payment_gateway_integration.service.StripePaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final StripePaymentService stripePaymentService;

    @PostMapping("/attach-method")
    public ResponseEntity<PaymentMethodResponseDTO> attachPaymentMethod(@RequestBody PaymentRequestDTO requestDTO) throws StripeException {
        PaymentMethod paymentMethod = stripePaymentService.attachPaymentMethodToCustomer(
                requestDTO.getPaymentMethodId(),
                requestDTO.getStripeCustomerId()
        );

        PaymentMethodResponseDTO responseDTO = new PaymentMethodResponseDTO(
                paymentMethod.getId(),
                paymentMethod.getType(),
                paymentMethod.getCustomer()
        );

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<PaymentIntent> createPayment(@RequestBody PaymentRequestDTO requestDTO) throws StripeException {
        PaymentIntent paymentIntent = stripePaymentService.chargeCustomer(
                requestDTO.getStripeCustomerId(),
                requestDTO.getAmount(),
                requestDTO.getPaymentMethodId()
        );
        return ResponseEntity.ok(paymentIntent);
    }
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getPaymentById(@PathVariable Long id) {
        TransactionResponseDTO responseDTO = stripePaymentService.getPaymentById(id);
        return ResponseEntity.ok(responseDTO);
    }
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllPayments() {
        List<TransactionResponseDTO> responseDTOs = stripePaymentService.getAllPayments();
        return ResponseEntity.ok(responseDTOs);
    }
}



