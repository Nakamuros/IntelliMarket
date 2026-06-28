package com.intellimarket.api.order.service;

import com.intellimarket.api.order.dto.*;
import com.intellimarket.api.payments.dto.PaymentStatusRequest;
import com.intellimarket.api.payments.dto.PaymentsResponse;

import java.util.List;

public interface IOrderService {
    // Gestion del Carrito
    CartResponseDTO getCartByEmail(String email);
    CartResponseDTO addItemToCartByEmail(String email, AddToCartRequestDTO request);
    // NUEVO: actualiza solo la cantidad de un CartItem existente, identificado por su propio id
    CartResponseDTO updateCartItemQuantity(String email, Long itemId, UpdateCartItemRequestDTO request);
    // NUEVO: faltaba la implementación; el frontend ya llamaba a este endpoint
    CartResponseDTO removeItemFromCart(String email, Long itemId);
    void clearCartByEmail(String email);

    // Gestion de Ordenes
    List<OrderResponseDTO> placeOrderByEmail(String email, OrderRequestDTO request);
    List<OrderResponseDTO> getOrderHistoryByEmail(String email);
    OrderResponseDTO getOrderById(Long orderId);

    // Generar un pago al efectuarse
    PaymentsResponse seePayment(String email, Long orderId);
    PaymentsResponse updatePaymentStatus(String email, Long orderId, PaymentStatusRequest request);
}