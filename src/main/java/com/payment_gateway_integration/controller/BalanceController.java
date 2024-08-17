package com.payment_gateway_integration.controller;

import com.payment_gateway_integration.dto.request.BalanceRequestDTO;
import com.payment_gateway_integration.dto.response.BalanceResponseDTO;
import com.payment_gateway_integration.service.StripeBalanceService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final StripeBalanceService stripeBalanceService;

    @PostMapping("/top-up/{customerId}")
    public ResponseEntity<BalanceResponseDTO> topUpBalance(
            @RequestBody BalanceRequestDTO requestDTO, @PathVariable Long customerId) throws StripeException {
        BalanceResponseDTO responseDTO = stripeBalanceService.topUpBalance(requestDTO, customerId);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<BalanceResponseDTO> getBalance(@PathVariable Long customerId) throws StripeException {
        BalanceResponseDTO responseDTO = stripeBalanceService.getBalance(customerId);
        return ResponseEntity.ok(responseDTO);
    }
}








