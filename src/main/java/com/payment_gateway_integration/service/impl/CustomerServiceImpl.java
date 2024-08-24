package com.payment_gateway_integration.service.impl;

import com.payment_gateway_integration.dto.request.CustomerRequestDTO;
import com.payment_gateway_integration.dto.response.CustomerResponseDTO;
import com.payment_gateway_integration.entity.Customer;
import com.payment_gateway_integration.exception.PayPalCustomException;
import com.payment_gateway_integration.repository.CustomerRepository;
import com.payment_gateway_integration.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        Customer customer = new Customer();
        customer.setName(customerRequestDTO.getName());
        customer.setEmail(customerRequestDTO.getEmail());
        customer.setBalance(customerRequestDTO.getInitialBalance());

        return convertToCustomerResponseDTO(customerRepository.save(customer));
    }

    @Override
    public CustomerResponseDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::convertToCustomerResponseDTO)
                .orElseThrow(() -> new PayPalCustomException("Customer not found", PayPalCustomException.ErrorCode.CUSTOMER_NOT_FOUND));
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToCustomerResponseDTO)
                .collect(Collectors.toList());
    }

    private CustomerResponseDTO convertToCustomerResponseDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setBalance(customer.getBalance());

        return dto;
    }
}
