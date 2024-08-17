package com.payment_gateway_integration.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transactionId;
    private Double amount;
    private String status;
    private Long customerId; // Add this field to reference the customer

    public Transaction() {}

    public Transaction(String transactionId, Double amount, String status, Long customerId) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.status = status;
        this.customerId = customerId;
    }
}

