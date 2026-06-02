package com.intellimarket.api.payments.dto;

import com.intellimarket.api.payments.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record PaymentStatusRequest(
        @NotNull(message = "El estado del pago es obligatorio")
        PaymentStatus status
) {}
