package com.intellimarket.api.inventory.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID de producto del inventario

    @Column(nullable=false)
    private String name; // Nombre de producto del inventario

    @Column(nullable=false)
    private String description; // Descripción de un producto

    @Column(nullable=false)
    private Double price; // Precio de un producto

    @Column(nullable=false)
    private Integer stock; // Stock de un producto determinado
}
