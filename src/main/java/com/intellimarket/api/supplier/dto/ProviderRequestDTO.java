package com.intellimarket.api.supplier.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ProviderRequestDTO(
        @NotBlank(message = "El nombre del proveedor es obligatorio")
        String name,
        
        @NotBlank(message = "El nombre de contacto es obligatorio")
        String contactName,

        List<Long> productIds
) {}