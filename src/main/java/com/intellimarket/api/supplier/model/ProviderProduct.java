package com.intellimarket.api.supplier.model;

import com.intellimarket.api.product.model.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "provider_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderProduct {

    @EmbeddedId
    private ProviderProductId id = new ProviderProductId();

    @ManyToOne
    @MapsId("providerId")
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;
}