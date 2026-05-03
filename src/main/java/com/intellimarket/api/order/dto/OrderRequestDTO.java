package com.intellimarket.api.order.dto;

import jakarta.validation.constraints.*;

public record OrderRequestDTO(
        @NotNull(message = "El id de la tienda es obligatorio")
        Long storeId
) {}
