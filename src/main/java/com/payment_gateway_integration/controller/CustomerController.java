package com.payment_gateway_integration.controller;

import com.payment_gateway_integration.dto.request.CustomerRequestDTO;
import com.payment_gateway_integration.dto.response.CustomerResponseDTO;
import com.payment_gateway_integration.entity.CustomerProfile;
import com.payment_gateway_integration.service.StripeCustomerService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final StripeCustomerService stripeCustomerService;

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody CustomerRequestDTO requestDTO) {
        try {
            CustomerProfile profile = new CustomerProfile(requestDTO.getName(), requestDTO.getEmail());
            com.stripe.model.Customer customer = stripeCustomerService.createCustomer(profile);

            CustomerResponseDTO responseDTO = stripeCustomerService.getCustomerById(profile.getId());

            return ResponseEntity.ok(responseDTO);
        } catch (StripeException e) {
            System.out.println("Exception :" +e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        CustomerResponseDTO responseDTO = stripeCustomerService.getCustomerById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> responseDTOs = stripeCustomerService.getAllCustomers();
        return ResponseEntity.ok(responseDTOs);
    }
}
