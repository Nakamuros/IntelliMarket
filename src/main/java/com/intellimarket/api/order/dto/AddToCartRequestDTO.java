package com.intellimarket.api.order.dto;

import jakarta.validation.constraints.*;

public record AddToCartRequestDTO(
        @NotNull(message = "El id del carrito es obligatorio")
        Long cartId,
        @NotNull(message = "El id del producto es obligatorio")
        Long productId,
        @NotNull(message = "El id de la tienda es obligatorio")
        Long storeId,
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad mínima debe ser 1")
        Integer quantity
){}
