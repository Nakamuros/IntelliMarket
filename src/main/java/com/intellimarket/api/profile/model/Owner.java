package com.intellimarket.api.profile.model;

import com.intellimarket.api.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "owner_profile")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Owner {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "dni", length = 15)
    private String dni;
}
