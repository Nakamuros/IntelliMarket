package com.intellimarket.api.payments.dto;

import com.intellimarket.api.payments.model.Method;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentsRequest(
        @NotNull
        Long orderId,

        @NotNull
        Method method,

        @NotBlank
        String status,

        @NotNull
        BigDecimal amount

) {
}
