package com.intellimarket.api.payments.repository;

import com.intellimarket.api.payments.model.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentsRepository extends JpaRepository<Payments, Long> {
    // Hola
    Optional<Payments> findByOrderId(Long id);
}

