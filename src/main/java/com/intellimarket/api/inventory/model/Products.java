package com.intellimarket.api.inventory.model;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name="products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID de producto del inventario

    @Column(nullable=false, length=60)
    private String name; // Nombre de producto del inventario

    @Column(nullable=false, length=130)
    private String description; // Descripción de un producto

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;


    @Column(name = "created_at")
    private LocalDateTime createdAt; // Timestamp

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
