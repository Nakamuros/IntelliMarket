package com.intellimarket.api.auth.dto.request;

import com.intellimarket.api.auth.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es valido")
        String email,

        @NotBlank(message = "La clave es obligatoria")
        @Email(message = "El formato del email no es valido")
        String password,

        @NotNull(message = "El rol es obligatorio (CUSTOMER o SELLER")
        Role role
) { }
