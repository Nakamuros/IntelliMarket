package com.intellimarket.api.payments.model;

import com.intellimarket.api.order.model.Order;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Payments {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="order_id", nullable=false)
    private Order order;

    @Column(name = "external_reference", nullable = false, length = 100)
    private String externalReference;

    @Enumerated(EnumType.STRING)
    @Column(name="method", nullable=false)
    private Method method;

    @Column(name="status", nullable=false)
    private String status;

    @Column(name="amount", nullable=false)
    private BigDecimal amount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
