package com.intellimarket.api.supplier.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProviderProductId implements Serializable {

    @Column(name = "provider_id")
    private Long providerId;

    @Column(name = "product_id")
    private Long productId;
}