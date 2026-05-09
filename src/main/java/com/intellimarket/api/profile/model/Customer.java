
package com.intellimarket.api.profile.model;

import com.intellimarket.api.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_profile") // Respetando el nombre del diagrama
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    private Long id; // Representa el user_id (Bigint). No lleva @GeneratedValue.

    // Relación 1 a 1 obligatoria con USERS
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Le dice a Hibernate: "Usa el ID de 'user' como mi propia llave primaria (PK/FK)"
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "address", length = 255)
    private String address;
}