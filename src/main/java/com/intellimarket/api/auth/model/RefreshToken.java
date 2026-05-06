package com.intellimarket.api.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Este es el pase largo (un código UUID único)
    @Column(nullable = false, unique = true, length = 100)
    private String token;

    // A qué usuario le pertenece
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Cuándo caduca (7 días)
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // Si el usuario cierra sesión, esto se vuelve "true" y el token muere
    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}