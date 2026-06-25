package com.intellimarket.api.ai.dto;

import jakarta.validation.constraints.NotBlank;

public record AiRequest(
        @NotBlank(message = "The message cannot be empty.")
        String message
) {}
