package com.intellimarket.api.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        String status,
        String storeName,
        Long storeId,
        BigDecimal totalAmount,
        List<OrderItemResponseDTO> items,
        LocalDateTime createdAt
) {}
