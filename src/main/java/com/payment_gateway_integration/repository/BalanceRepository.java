package com.payment_gateway_integration.repository;

import com.payment_gateway_integration.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByAccountHolderId(Long accountHolderId);
}
