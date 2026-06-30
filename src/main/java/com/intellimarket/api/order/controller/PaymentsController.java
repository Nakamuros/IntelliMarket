package com.intellimarket.api.order.controller;

import com.intellimarket.api.order.service.IOrderService;
import com.intellimarket.api.payments.dto.PaymentStatusRequest;
import com.intellimarket.api.payments.dto.PaymentsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// FIX: la clase estaba completamente vacía (sin @RestController ni rutas),
// por eso Spring nunca registraba /api/payments/** y el navegador interpretaba
// la ausencia de respuesta como un bloqueo de CORS.
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentsController {

    private final IOrderService orderService;

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentsResponse> getPaymentByOrder(
            Authentication authentication,
            @PathVariable Long orderId) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.seePayment(email, orderId));
    }

    @PatchMapping("/order/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentsResponse> updatePaymentStatus(
            Authentication authentication,
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentStatusRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.updatePaymentStatus(email, orderId, request));
    }
}