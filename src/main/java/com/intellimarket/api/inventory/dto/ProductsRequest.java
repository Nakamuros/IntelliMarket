package com.intellimarket.api.inventory.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductsRequest (
        @NotBlank
        @Size(max = 60)
        String name,

        @NotBlank
        String category,

        @NotBlank
        @Size(max = 130)
        String description,

        @NotNull
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        @DecimalMax(value = "200.00", message = "El precio no puede superar 200")
        BigDecimal price,

        @NotNull
        @Min(value = 0, message = "El stock inicial no puede ser negativo")
        Integer stock,

        @NotNull
        Long store_id,

        @NotNull
        Long product_id
) {}
