package com.payment_gateway_integration.service.impl;

import com.payment_gateway_integration.dto.request.BalanceRequestDTO;
import com.payment_gateway_integration.dto.response.BalanceResponseDTO;
import com.payment_gateway_integration.entity.Balance;
import com.payment_gateway_integration.entity.CustomerProfile;
import com.payment_gateway_integration.repository.BalanceRepository;
import com.payment_gateway_integration.repository.CustomerRepository;
import com.payment_gateway_integration.service.StripeBalanceService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeBalanceServiceImpl implements StripeBalanceService {

    private final BalanceRepository accountBalanceRepository;
    private final CustomerRepository customerProfileRepository;

    @Override
    public BalanceResponseDTO topUpBalance(BalanceRequestDTO requestDTO, Long customerId) throws StripeException {
        CustomerProfile customerProfile = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Account holder not found"));

        // Create or update the Balance
        Balance accountBalance = accountBalanceRepository.findByAccountHolderId(customerId)
                .orElseGet(() -> createAccountBalance(customerId));

        // Create a PaymentIntent with a payment method
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(requestDTO.getTopUpAmount() * 100L)
                        .setCurrency("usd")
                        .setCustomer(customerProfile.getStripeCustomerId())
                        .setPaymentMethod(requestDTO.getPaymentMethodId())
                        .setConfirm(true)
                        .setDescription("Top-up for customer: " + customerProfile.getName())
                        .build();

        PaymentIntent intent = PaymentIntent.create(params);

        // Check if the payment was successful before updating the balance
        if ("succeeded".equals(intent.getStatus())) {
            accountBalance.setAvailableAmount(accountBalance.getAvailableAmount() + requestDTO.getTopUpAmount());
            accountBalanceRepository.save(accountBalance);

            // Return the updated balance
            return mapToResponseDTO(accountBalance);
        } else {
            throw new RuntimeException("Payment failed, status: " + intent.getStatus());
        }
    }

    private Balance createAccountBalance(Long customerId) {
        Balance accountBalance = new Balance();
        accountBalance.setAccountHolderId(customerId);
        accountBalance.setAvailableAmount(0L);
        accountBalance.setPendingAmount(0L);
        return accountBalanceRepository.save(accountBalance);
    }

    @Override
    public BalanceResponseDTO getBalance(Long customerId) {
        Balance accountBalance = accountBalanceRepository.findByAccountHolderId(customerId)
                .orElseThrow(() -> new RuntimeException("Account balance not found"));

        return mapToResponseDTO(accountBalance);
    }

    private BalanceResponseDTO mapToResponseDTO(Balance balance) {
        BalanceResponseDTO responseDTO = new BalanceResponseDTO(balance.getAvailableAmount(),balance.getPendingAmount() );
        responseDTO.setAvailable(balance.getAvailableAmount());
        responseDTO.setPending(balance.getPendingAmount());
        return responseDTO;
    }
    @Override
    public BalanceResponseDTO getBalanceForCustomer(Long customerId) {
        Balance balance = accountBalanceRepository.findByAccountHolderId(customerId)
                .orElseThrow(() -> new RuntimeException("Balance not found for customer"));

        return new BalanceResponseDTO(
                balance.getAvailableAmount(),
                balance.getPendingAmount()
        );
    }
}
