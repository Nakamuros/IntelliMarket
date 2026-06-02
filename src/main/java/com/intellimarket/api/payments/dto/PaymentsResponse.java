package com.intellimarket.api.payments.dto;

import com.intellimarket.api.payments.model.Method;

import java.time.LocalDate;

public record PaymentsResponse(
        Long id,
        String externalReference,
        Long orderId,
        Method method,
        String status,
        LocalDate createdAt
) {
}
