package com.payment_gateway_integration.service;

import com.payment_gateway_integration.dto.response.CustomerResponseDTO;
import com.payment_gateway_integration.entity.CustomerProfile;
import com.stripe.exception.StripeException;

import java.util.List;

public interface StripeCustomerService {
    com.stripe.model.Customer createCustomer(CustomerProfile customerProfile) throws StripeException;
    CustomerResponseDTO getCustomerById(Long id);
    List<CustomerResponseDTO> getAllCustomers();
}
