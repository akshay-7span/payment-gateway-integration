package com.payment_gateway_integration.service;

import com.payment_gateway_integration.dto.response.TransactionResponseDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;

import java.util.List;

public interface StripePaymentService {

    PaymentMethod attachPaymentMethodToCustomer(String paymentMethodId, String stripeCustomerId) throws StripeException;

    PaymentIntent chargeCustomer(String stripeCustomerId, Double amount, String paymentMethodId) throws StripeException;

    TransactionResponseDTO getPaymentById(Long id);

    List<TransactionResponseDTO> getAllPayments();
}
