package com.intellimarket.api.auth.dto;

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
    private String token;
    private String accessToken;
    private String refreshToken;

    public static AuthResponse fromUser(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}

