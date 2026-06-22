package com.intellimarket.api.inventory.dto;

import java.math.BigDecimal;

public record ProductsResponse (
        String name,
        String category,
        String description,
        Integer stock,
        BigDecimal unitPrice,
        Long id,
        Integer status,
        String imageUrl
) {}
