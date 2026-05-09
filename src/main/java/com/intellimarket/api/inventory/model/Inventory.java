package com.intellimarket.api.inventory.model;

import com.intellimarket.api.stores.model.Stores;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import org.apache.catalina.Store;

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
    private Products product; // Relación: Products listed in Inventory

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    //@Column(name = "store_id", nullable = false)
    private Stores store; // ID de la tienda (Store has Inventory)
    //private Long storeId;
    // Store

    @Column(nullable = false)
    private Integer stock;

    //@Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private Integer state;

    @DecimalMin("0.10")
    @DecimalMax("200.00")
    @Column(nullable = false, precision=10, scale=2)
    private BigDecimal price;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
