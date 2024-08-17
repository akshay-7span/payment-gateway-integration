package com.payment_gateway_integration.service.impl;

import com.payment_gateway_integration.dto.response.TransactionResponseDTO;
import com.payment_gateway_integration.entity.Transaction;
import com.payment_gateway_integration.repository.TransactionRepository;
import com.payment_gateway_integration.service.StripePaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StripePaymentServiceImpl implements StripePaymentService {

    private final TransactionRepository transactionRepository;

    @Override
    public PaymentMethod attachPaymentMethodToCustomer(String paymentMethodId, String stripeCustomerId) throws StripeException {
        PaymentMethodAttachParams params = PaymentMethodAttachParams.builder()
                .setCustomer(stripeCustomerId)
                .build();

        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        return paymentMethod.attach(params);
    }

    @Override
    public PaymentIntent chargeCustomer(String stripeCustomerId, Double amount, String paymentMethodId) throws StripeException {
        // Attach payment method to the customer
        PaymentMethod paymentMethod = attachPaymentMethodToCustomer(paymentMethodId, stripeCustomerId);

        // Create Payment Intent
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (amount * 100))  // Stripe expects amount in cents
                .setCurrency("usd")
                .setCustomer(stripeCustomerId)
                .setPaymentMethod(paymentMethodId)
                .setOffSession(true)
                .setConfirm(true)
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Save transaction to the database, including the customerId
        Transaction transaction = new Transaction(paymentIntent.getId(), amount, paymentIntent.getStatus(), Long.parseLong(stripeCustomerId));
        transactionRepository.save(transaction);

        return paymentIntent;
    }

    @Override
    public TransactionResponseDTO getPaymentById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return mapToResponseDTO(transaction);
    }

    @Override
    public List<TransactionResponseDTO> getAllPayments() {
        return transactionRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getTransactionId(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getCustomerId()
        );
    }
}
