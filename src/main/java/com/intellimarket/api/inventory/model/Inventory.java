package com.intellimarket.api.inventory.model;

import com.intellimarket.api.product.model.Product;
import com.intellimarket.api.store.model.Store;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    //@Id
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Relación: Products listed in Inventory

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    //@Column(name = "store_id", nullable = false)
    private Store store; // ID de la tienda (Store has Inventory)
    //private Long storeId;
    // Store

    @Column(nullable = false)
    private Integer stock;

    // Availability flag kept for compatibility with the existing schema.
    @Column(name = "state", nullable = false)
    private Integer state;

    @DecimalMin("0.10")
    @DecimalMax("200.00")
    @Column(nullable = false, precision=10, scale=2)
    private BigDecimal price;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        syncAvailability();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        syncAvailability();
        updatedAt = LocalDateTime.now();
    }

    private void syncAvailability() {
        state = (stock != null && stock > 0) ? 1 : 0;
    }
}
