package com.intellimarket.api.profile.dto;

import jakarta.validation.constraints.Size;

public record OwnerProfileRequest(
        @Size(max = 15, message = "El telefono no puede exceder los 15 caracteres")
        String phone,

        @Size(max = 8, message = "El dni no puede tener mas de 8 numeros")
        String dni
) { }
