package com.payment_gateway_integration.service;

import com.payment_gateway_integration.dto.request.CustomerRequestDTO;
import com.payment_gateway_integration.dto.request.TransferRequestDTO;
import com.payment_gateway_integration.dto.response.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {
    CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO);

    CustomerResponseDTO getCustomerById(Long id);

    List<CustomerResponseDTO> getAllCustomers();

}

