package com.intellimarket.api.product.model;

import com.intellimarket.api.inventory.model.Category;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(length = 130)
    private String description;

    @Column
    private String image;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    // Legacy snapshot fields kept for schema compatibility.
    // Store-specific stock is managed by Inventory.
    /*@Column(nullable = false, updatable = false)
    @Builder.Default
    private Integer stock = 0;*/

    /*@Column(nullable = false, updatable = false)
    @Builder.Default
    private Integer status = 1; // 1: Disponible, 0: Agotado*/

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
