package com.payment_gateway_integration.service.impl;

import com.payment_gateway_integration.dto.response.*;
import com.payment_gateway_integration.entity.Balance;
import com.payment_gateway_integration.entity.CustomerProfile;
import com.payment_gateway_integration.entity.Transaction;
import com.payment_gateway_integration.repository.BalanceRepository;
import com.payment_gateway_integration.repository.CustomerRepository;
import com.payment_gateway_integration.repository.TransactionRepository;
import com.payment_gateway_integration.service.StripeBalanceService;
import com.payment_gateway_integration.service.StripeCustomerService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentMethodListParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StripeCustomerServiceImpl implements StripeCustomerService {

    private final CustomerRepository customerProfileRepository;
    private final TransactionRepository transactionRepository;
    private final BalanceRepository balanceRepository;
    private final StripeBalanceService balanceService;

    @Override
    public com.stripe.model.Customer createCustomer(CustomerProfile customerProfile) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(customerProfile.getEmail())
                .setName(customerProfile.getName())
                .build();

        com.stripe.model.Customer customer = com.stripe.model.Customer.create(params);
        customerProfile.setStripeCustomerId(customer.getId());
        customerProfileRepository.save(customerProfile);
        return customer;
    }

    @Override
    public CustomerResponseDTO getCustomerById(Long id) {
        CustomerProfile customerProfile = customerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account holder not found"));

        // Fetch balance, if available
        Optional<Balance> balance = balanceRepository.findByAccountHolderId(id);
        BalanceResponseDTO balanceResponse = balance.map(b -> new BalanceResponseDTO(
                b.getAvailableAmount(),
                b.getPendingAmount()
        )).orElse(null);

        // Fetch payment methods from Stripe
        List<PaymentMethodResponseDTO> paymentMethods = getPaymentMethods(customerProfile.getStripeCustomerId());

        // Fetch transactions
        List<Transaction> transactions = transactionRepository.findByCustomerId(id);

        return new CustomerResponseDTO(
                customerProfile.getStripeCustomerId(),
                customerProfile.getName(),
                customerProfile.getEmail(),
                paymentMethods,
                balanceResponse,
                transactions
        );
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerProfileRepository.findAll().stream()
                .map(customerProfile -> {
                    Optional<Balance> balance = balanceRepository.findByAccountHolderId(customerProfile.getId());
                    BalanceResponseDTO balanceResponse = balance.map(b -> new BalanceResponseDTO(
                            b.getAvailableAmount(),
                            b.getPendingAmount()
                    )).orElse(null);

                    List<PaymentMethodResponseDTO> paymentMethods = getPaymentMethods(customerProfile.getStripeCustomerId());

                    List<Transaction> transactions = transactionRepository.findByCustomerId(customerProfile.getId());

                    return new CustomerResponseDTO(
                            customerProfile.getStripeCustomerId(),
                            customerProfile.getName(),
                            customerProfile.getEmail(),
                            paymentMethods,
                            balanceResponse,
                            transactions
                    );
                })
                .collect(Collectors.toList());
    }

    // Method to retrieve payment methods from Stripe
    private List<PaymentMethodResponseDTO> getPaymentMethods(String stripeCustomerId) {
        try {
            PaymentMethodListParams params = PaymentMethodListParams.builder()
                    .setCustomer(stripeCustomerId)
                    .setType(PaymentMethodListParams.Type.CARD)  // Specify type if needed
                    .build();

            PaymentMethodCollection paymentMethods = PaymentMethod.list(params);

            return paymentMethods.getData().stream()
                    .map(pm -> new PaymentMethodResponseDTO(
                            pm.getId(),
                            pm.getType(),
                            pm.getCustomer()))
                    .collect(Collectors.toList());

        } catch (StripeException e) {
            System.out.println("Exception : "+e);
            return null;
        }
    }

    private CustomerResponseDTO mapToResponseDTO(CustomerProfile customerProfile) {
        List<PaymentMethodResponseDTO> paymentMethods = getPaymentMethods(customerProfile.getStripeCustomerId());
        BalanceResponseDTO balance = balanceService.getBalanceForCustomer(customerProfile.getId());
        List<Transaction> transactions = transactionRepository.findByCustomerId(customerProfile.getId());

        return new CustomerResponseDTO(
                customerProfile.getStripeCustomerId(),
                customerProfile.getName(),
                customerProfile.getEmail(),
                paymentMethods,
                balance,
                transactions
        );
    }

}
