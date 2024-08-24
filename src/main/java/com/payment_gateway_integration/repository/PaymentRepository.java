package com.payment_gateway_integration.repository;

import com.payment_gateway_integration.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findById(Long id);

    boolean existsByPaymentId(String paymentId);

    Optional<PaymentEntity> findByPaymentId(String paymentId);

}
