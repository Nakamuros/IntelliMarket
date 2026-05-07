package com.intellimarket.api.supplier.dto;

import jakarta.validation.constraints.NotBlank;

public record ProviderRequestDTO(
        @NotBlank(message = "El nombre del proveedor es obligatorio")
        String name,
        
        @NotBlank(message = "El nombre de contacto es obligatorio")
        String contactName
) {}