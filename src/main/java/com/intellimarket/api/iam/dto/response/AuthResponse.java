package com.intellimarket.api.iam.dto.response;

import com.intellimarket.api.iam.model.Role;

public record AuthResponse(
        Long id,
        String email,
        Role role
) {
}
