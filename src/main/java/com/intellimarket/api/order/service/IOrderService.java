package com.intellimarket.api.order.service;

import com.intellimarket.api.order.dto.*;
import java.util.List;

public interface IOrderService {
    // Gestion del Carrito
    CartResponseDTO getCart(Long userId);
    CartResponseDTO addItemToCart(Long userId, AddToCartRequestDTO request);
    void clearCart(Long userId);

    // Gestion de Ordenes
    OrderResponseDTO placeOrder(Long userId, OrderRequestDTO request);
    List<OrderResponseDTO> getOrderHistory(Long userId);
    OrderResponseDTO getOrderById(Long orderId);
}
