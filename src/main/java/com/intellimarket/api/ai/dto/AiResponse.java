package com.intellimarket.api.ai.dto;

public record AiResponse(
        boolean success,
        String summary
) {}