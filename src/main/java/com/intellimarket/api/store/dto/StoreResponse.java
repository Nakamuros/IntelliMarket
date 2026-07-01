package com.intellimarket.api.store.dto;

import java.time.LocalDateTime;

public record StoreResponse(
    Long id,
    String name,
    String address,
    String district,
    String ownerName,
    boolean isActive,
    LocalDateTime createdAt,
    String imageUrl
) {}
