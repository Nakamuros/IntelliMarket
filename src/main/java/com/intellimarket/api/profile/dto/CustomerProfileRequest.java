package com.intellimarket.api.profile.dto;

import jakarta.validation.constraints.Size;

public record CustomerProfileRequest(
        @Size(max = 15, message = "El telefono no puede exceder los 15 caracteres")
        String phone,

        @Size(max = 30, message = "La direccion no puede exceder los 30 caracteres")
        String address
) { }
