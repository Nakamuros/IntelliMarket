package com.intellimarket.api.inventory.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Products product_id; // Relación: Products listed in Inventory

    //@ManyToOne(fetch = FetchType.LAZY, optional = false)
    //@JoinColumn(name = "store_id", nullable = false)
    @Column(name = "store_id", nullable = false)
    private Long store_id; // ID de la tienda (Store has Inventory)
    // Store

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false, precision=10, scale=2)
    private BigDecimal price;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
