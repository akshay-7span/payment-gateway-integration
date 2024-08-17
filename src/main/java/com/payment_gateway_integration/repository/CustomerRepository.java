package com.payment_gateway_integration.repository;

import com.payment_gateway_integration.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerProfile, Long> {}
