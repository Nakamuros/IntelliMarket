package com.intellimarket.api.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// NUEVO: usado por PATCH /api/v1/orders/cart/items/{itemId}
// Solo necesita la nueva cantidad; el itemId viene en la URL.
public record UpdateCartItemRequestDTO(
        @NotNull
        @Min(value = 1, message = "La cantidad mínima es 1. Si deseas eliminar el producto, usa el botón de eliminar.")
        Integer quantity
) {}