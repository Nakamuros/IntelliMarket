package com.intellimarket.api.auth.dto.response;

import com.intellimarket.api.auth.model.Role;

public record AuthResponse(
        Long id,
        String email,
        Role role
) {
}
