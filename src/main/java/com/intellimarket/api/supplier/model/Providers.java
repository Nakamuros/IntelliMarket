package com.intellimarket.api.supplier.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PROVIDERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Providers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "contact_name", nullable = false)
    private String contactName;

    /*@ManyToMany
    @JoinTable(
            name = "PROVIDER_PRODUCTS",
            joinColumns = @JoinColumn(name = "provider_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Products> products;*/
}
