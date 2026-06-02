package com.intellimarket.api.order.controller;

import com.intellimarket.api.order.service.IOrderService;
import com.intellimarket.api.payments.dto.PaymentStatusRequest;
import com.intellimarket.api.payments.dto.PaymentsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentsController {
    private final IOrderService orderService;

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentsResponse> getPayment(Authentication auth,
                                                       @PathVariable Long orderId) {
        String email = auth.getName();
        PaymentsResponse response = orderService.seePayment(email, orderId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/order/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentsResponse> updatePaymentStatus(Authentication auth,
                                                                @PathVariable Long orderId,
                                                                @Valid @RequestBody PaymentStatusRequest request) {
        String email = auth.getName();
        PaymentsResponse response = orderService.updatePaymentStatus(email, orderId, request);
        return ResponseEntity.ok(response);
    }
}
