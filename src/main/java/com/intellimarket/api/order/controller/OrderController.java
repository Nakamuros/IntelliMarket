package com.intellimarket.api.order.controller;

import com.intellimarket.api.order.dto.*;
import com.intellimarket.api.order.service.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    //--Endpoints del carrito--
    @GetMapping("/cart/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponseDTO> getCart(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.getCartByEmail(email));
    }

    @PostMapping("/cart/items")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponseDTO> addItemToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequestDTO request) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.addItemToCartByEmail(email, request));
    }

    @DeleteMapping("/cart/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        String email = authentication.getName();
        orderService.clearCartByEmail(email);
        return ResponseEntity.noContent().build();
    }

    //--Endpoint de ordenes--
    // FIX: ya no recibe storeId en el body. El backend agrupa el carrito
    // por tienda automáticamente y genera una orden por cada tienda.
    @PostMapping("/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderResponseDTO>> placeOrder(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrderByEmail(email, new OrderRequestDTO()));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderResponseDTO>> getOrderHistory(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.getOrderHistoryByEmail(email));
    }
}