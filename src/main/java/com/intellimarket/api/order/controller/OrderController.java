package com.intellimarket.api.order.controller;

import com.intellimarket.api.order.dto.*;
import com.intellimarket.api.order.service.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    //--Endpoints del carrito--
    @GetMapping("/cart/{userId}")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getCart(userId));
    }

    @PostMapping("/cart/{userId}/items")
    public ResponseEntity<CartResponseDTO> addItemToCart(
            @PathVariable Long userId,
            @Valid @RequestBody AddToCartRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.addItemToCart(userId, request));
    }

    @DeleteMapping("/cart/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        orderService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    //--Endpoint de ordenes--
    @PostMapping("/{userId}")
    public ResponseEntity<OrderResponseDTO> placeOrder(
            @PathVariable Long userId,
            @Valid @RequestBody OrderRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).
                body(orderService.placeOrder(userId, request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrderHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }


}
