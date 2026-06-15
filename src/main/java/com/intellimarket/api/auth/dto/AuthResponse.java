package com.intellimarket.api.auth.dto;

import com.intellimarket.api.auth.model.Role;
import com.intellimarket.api.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;   // ← CAMPO AGREGADO
    private String token;

    public static AuthResponse fromUser(User user, String token) {
        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())   // ← MAPEADO DESDE USE
                .token(token)
                .build();
    }
}

