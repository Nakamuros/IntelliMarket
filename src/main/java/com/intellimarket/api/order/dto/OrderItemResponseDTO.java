package com.intellimarket.api.order.dto;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        Long id,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subtotal,
        String imageUrl
) {}
