package com.intellimarket.api.profile.model;

import com.intellimarket.api.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30) // Regla de Negocio RN-05
    private String storeName;

    @Column(nullable = false, length = 30) // Regla de Negocio RN-05
    private String storeAddress;

    @Column(length = 255)
    private String storeLogoUrl;

    @Column(length = 15)
    private String phone;

    // Relación 1 a 1 con tu tabla de usuarios
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;
}
