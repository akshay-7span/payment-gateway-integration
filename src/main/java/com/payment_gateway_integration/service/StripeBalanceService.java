package com.payment_gateway_integration.service;

import com.payment_gateway_integration.dto.request.BalanceRequestDTO;
import com.payment_gateway_integration.dto.response.BalanceResponseDTO;
import com.stripe.exception.StripeException;

public interface StripeBalanceService {
    BalanceResponseDTO topUpBalance(BalanceRequestDTO requestDTO, Long customerId) throws StripeException;
    BalanceResponseDTO getBalance(Long customerId) ;
    BalanceResponseDTO getBalanceForCustomer(Long customerId);
}






