package com.intellimarket.api.inventory.model;

import com.intellimarket.api.product.model.Product;
import com.intellimarket.api.store.model.Store;
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

public class InventoryMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Relación: Products affect Inventory_Movements

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    private Type type;

    @Column(name = "reference_id")
    private Long reference_id;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", nullable=false)
    private Reference_Type reference_type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
