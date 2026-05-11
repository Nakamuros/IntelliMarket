package com.intellimarket.api.order.service;

import com.intellimarket.api.order.dto.*;
import java.util.List;

public interface IOrderService {
    // Gestion del Carrito
    CartResponseDTO getCartByEmail(String email);
    CartResponseDTO addItemToCartByEmail(String email, AddToCartRequestDTO request);
    void clearCartByEmail(String email);

    // Gestion de Ordenes
    OrderResponseDTO placeOrderByEmail(String email, OrderRequestDTO request);
    List<OrderResponseDTO> getOrderHistoryByEmail(String email);
    OrderResponseDTO getOrderById(Long orderId);
}
