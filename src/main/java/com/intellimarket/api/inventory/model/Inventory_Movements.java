package com.intellimarket.api.inventory.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="inventory_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Inventory_Movements {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products product_id; // Relación: Products affect Inventory_Movements

    @Column(name = "store_id", nullable = false)
    private Long store_id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String type;

    @Column(name = "reference_id")
    private Long reference_id;

    @Column(name = "reference_type")
    private String reference_type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
