package com.intellimarket.api.inventory.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public record ProductsResponse (
        String name,
        String category,
        String description,
        Integer stock,
        BigDecimal price,
        BigInteger id
) {}
