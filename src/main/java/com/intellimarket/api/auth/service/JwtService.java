package com.intellimarket.api.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Lee las variables que pusiste en tu application.properties
    @Value("${intellimarket.security.jwt.secret}")
    private String secret;

    @Value("${intellimarket.security.jwt.expiration-ms}")
    private long expirationMs;

    // Genera el Token cuando el usuario hace login
    public String generateToken(UserDetails user, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // Guardamos el rol dentro del token para validaciones rápidas

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername()) // Guardamos el email
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // Expira en 15 min
                .signWith(getSigningKey()) // Lo firmamos con tu clave secreta
                .compact();
    }

    // Extrae el correo del Token que nos envía el cliente
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Verifica si el Token es válido y le pertenece a ese usuario
    public boolean isValid(String token, UserDetails user) {
        final String email = extractEmail(token);
        return (email.equals(user.getUsername())) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }

    // Transforma tu texto secreto en una llave criptográImplfica real
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(secret.getBytes()));
        return Keys.hmacShaKeyFor(keyBytes);
    }
}