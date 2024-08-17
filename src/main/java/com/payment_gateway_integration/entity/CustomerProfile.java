package com.payment_gateway_integration.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class CustomerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String stripeCustomerId;
    public CustomerProfile() {}

    public CustomerProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
