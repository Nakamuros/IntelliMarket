package com.intellimarket.api.payments.model;

import com.intellimarket.api.order.model.Orders;
import com.intellimarket.api.store.model.Store;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID de payments o pagos

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders; // ID de la orden

    private Method method; // Ejemplo: "CARD", "CASH", "TRANSFER"

    private String status;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
