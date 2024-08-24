package com.payment_gateway_integration.service.impl;

import com.payment_gateway_integration.dto.PaymentDTO;
import com.payment_gateway_integration.dto.request.PaymentExecutionRequestDTO;
import com.payment_gateway_integration.dto.request.PaymentRequestDTO;
import com.payment_gateway_integration.entity.Customer;
import com.payment_gateway_integration.entity.PaymentEntity;
import com.payment_gateway_integration.exception.PayPalCustomException;
import com.payment_gateway_integration.repository.CustomerRepository;
import com.payment_gateway_integration.repository.PaymentRepository;
import com.payment_gateway_integration.service.PaymentService;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final APIContext apiContext;

    public PaymentServiceImpl(CustomerRepository customerRepository, PaymentRepository paymentRepository, APIContext apiContext) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.apiContext = apiContext;
    }

    @Override
    @Transactional
    public PaymentDTO processPayment(PaymentRequestDTO paymentRequestDTO) {
        Customer customer = customerRepository.findById(paymentRequestDTO.getCustomerId()).orElseThrow(() -> new PayPalCustomException("Customer not found", PayPalCustomException.ErrorCode.CUSTOMER_NOT_FOUND));

        if (customer.getBalance() < paymentRequestDTO.getAmount()) {
            throw new PayPalCustomException("Insufficient balance for this payment", PayPalCustomException.ErrorCode.INSUFFICIENT_BALANCE);
        }

        // Create PayPal Payment
        Payment createdPayPalPayment = createPayPalPayment(paymentRequestDTO);
        System.out.println("Created PayPal Payment: " + createdPayPalPayment);
        String paymentId = createdPayPalPayment.getId();
        System.out.println("Created PayPal Payment ID: " + paymentId);

        String approvalLink = createdPayPalPayment.getLinks().stream().filter(link -> "approval_url".equalsIgnoreCase(link.getRel())).findFirst().map(Links::getHref).orElseThrow(() -> new PayPalCustomException("Failed to obtain approval URL from PayPal", PayPalCustomException.ErrorCode.PAYPAL_PAYMENT_FAIL));

        if (paymentRepository.existsByPaymentId(createdPayPalPayment.getId())) {
            throw new PayPalCustomException("A payment with this PayPal ID already exists.", PayPalCustomException.ErrorCode.PAYPAL_PAYMENT_DUPLICATE);
        }

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setPaymentId(createdPayPalPayment.getId());
        paymentEntity.setAmount(paymentRequestDTO.getAmount());
        paymentEntity.setPaymentMethod("PAYPAL");
        paymentEntity.setStatus("CREATED");
        paymentEntity.setPaymentDate(LocalDateTime.now());
        paymentEntity.setApprovalUrl(approvalLink);
        paymentEntity.setCustomer(customer);

        PaymentEntity savedPayment = paymentRepository.save(paymentEntity);

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setId(savedPayment.getId());
        paymentDTO.setAmount(savedPayment.getAmount());
        paymentDTO.setPaymentMethod(savedPayment.getPaymentMethod());
        paymentDTO.setPaymentDate(savedPayment.getPaymentDate());
        paymentDTO.setStatus(savedPayment.getStatus());
        paymentDTO.setApprovalUrl(savedPayment.getApprovalUrl());
        paymentDTO.setPaymentId(savedPayment.getPaymentId());


        return paymentDTO;
    }

    @Override
    @Transactional
    public PaymentDTO executePayment(PaymentExecutionRequestDTO paymentExecutionRequestDTO) {
        Payment executedPayPalPayment = executePayPalPayment(paymentExecutionRequestDTO.getPaymentId(), paymentExecutionRequestDTO.getPayerId());

        String invoiceNumber = executedPayPalPayment.getTransactions().getFirst().getInvoiceNumber();
        System.out.println("Invoice Number: " + invoiceNumber);

        if (invoiceNumber == null || invoiceNumber.isEmpty()) {
            throw new PayPalCustomException("Invoice number is missing", PayPalCustomException.ErrorCode.PAYPAL_PAYMENT_FAIL);
        }

        long customerId;
        try {
            customerId = Long.parseLong(invoiceNumber);
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse invoice number: " + invoiceNumber);
            throw new PayPalCustomException("Invalid invoice number format", PayPalCustomException.ErrorCode.PAYPAL_PAYMENT_FAIL);
        }

        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new PayPalCustomException("Customer not found", PayPalCustomException.ErrorCode.CUSTOMER_NOT_FOUND));

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setPaymentId(paymentExecutionRequestDTO.getPaymentId());
        paymentEntity.setPaymentDate(LocalDateTime.now());
        paymentEntity.setAmount(Double.parseDouble(executedPayPalPayment.getTransactions().getFirst().getAmount().getTotal()));
        paymentEntity.setPaymentMethod(executedPayPalPayment.getPayer().getPaymentMethod());
        paymentEntity.setStatus(executedPayPalPayment.getState());
        paymentEntity.setCustomer(customer);

        customer.setBalance(customer.getBalance() - paymentEntity.getAmount());
        customerRepository.save(customer);
        PaymentEntity savedPayment = paymentRepository.save(paymentEntity);

        return convertToPaymentDTO(savedPayment);
    }


    private Payment createPayPalPayment(PaymentRequestDTO paymentRequestDTO) {
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.format("%.2f", paymentRequestDTO.getAmount()));

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription("Payment for order");

        transaction.setInvoiceNumber(paymentRequestDTO.getCustomerId() + "-" + System.currentTimeMillis());

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setRedirectUrls(getRedirectURLs());

        try {
            Payment createdPayment = payment.create(apiContext);
            System.out.println("Created PayPal Payment ID: " + createdPayment.getId());
            return createdPayment;
        } catch (Exception e) {
            System.err.println("Error creating PayPal payment: " + e.getMessage());
            e.printStackTrace();
            throw new PayPalCustomException("Failed to create PayPal payment: " + e.getMessage(), PayPalCustomException.ErrorCode.PAYPAL_PAYMENT_FAIL);
        }
    }


    private RedirectUrls getRedirectURLs() {
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("https://example.com/cancel");
        redirectUrls.setReturnUrl("https://example.com/execute");
        return redirectUrls;
    }

    private Payment executePayPalPayment(String paymentId, String payerId) {
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        Payment payment = new Payment();
        payment.setId(paymentId);

        try {
            Payment executedPayment = payment.execute(apiContext, paymentExecution);
            System.out.println("Executed PayPal Payment Response: " + executedPayment.toJSON());

            return payment.execute(apiContext, paymentExecution);
        } catch (PayPalRESTException e) {
            if (e.getDetails() != null && "PAYMENT_ALREADY_DONE".equals(e.getDetails().getName())) {
                throw new PayPalCustomException("Payment has already been completed for this cart.", PayPalCustomException.ErrorCode.PAYMENT_ALREADY_DONE);
            }
            throw new PayPalCustomException("Failed to execute PayPal payment: " + e.getMessage(), PayPalCustomException.ErrorCode.PAYPAL_PAYMENT_FAIL);
        }

    }

    private PaymentDTO convertToPaymentDTO(PaymentEntity payment) {
        if (payment == null) {
            return null;
        }

        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setStatus(payment.getStatus());
        dto.setApprovalUrl(payment.getApprovalUrl());

        return dto;
    }
}
