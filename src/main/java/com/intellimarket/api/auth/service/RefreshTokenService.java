package com.intellimarket.api.auth.service;

import com.intellimarket.api.auth.model.RefreshToken;
import com.intellimarket.api.auth.model.User;
import com.intellimarket.api.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${intellimarket.security.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Transactional
    public RefreshToken create(User user) {
        RefreshToken rt = RefreshToken.builder()
                .token(UUID.randomUUID().toString()) // Generamos un código único
                .user(user)
                .expiresAt(LocalDateTime.now().plusNanos(refreshExpirationMs * 1_000_000))
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();
        return refreshTokenRepository.save(rt);
    }

    @Transactional
    public RefreshToken validate(String token) {
        RefreshToken rt = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token no encontrado"));

        if (rt.isRevoked()) {
            // Si alguien intenta usar un token ya revocado, ¡alerta de robo!
            // Matamos todos los tokens del usuario por seguridad
            refreshTokenRepository.revokeAllByUserId(rt.getUser().getId());
            throw new RuntimeException("Token revocado. Posible intento de fraude.");
        }

        if (rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expirado");
        }
        return rt;
    }

    @Transactional
    public RefreshToken rotate(RefreshToken current) {
        current.setRevoked(true); // Matamos el actual
        refreshTokenRepository.save(current);
        return create(current.getUser()); // Creamos uno nuevo
    }

    @Transactional
    public void revoke(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }
}