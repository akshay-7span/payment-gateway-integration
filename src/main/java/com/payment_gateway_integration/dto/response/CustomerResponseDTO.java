package com.payment_gateway_integration.dto.response;

import com.payment_gateway_integration.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class CustomerResponseDTO {
    private String id;
    private String name;
    private String email;
    private List<PaymentMethodResponseDTO> paymentMethods;
    private BalanceResponseDTO balance;
    private List<Transaction> transactions;
}
