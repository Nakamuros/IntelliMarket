package com.intellimarket.api.order.service;

import com.intellimarket.api.order.dto.*;
import com.intellimarket.api.payments.dto.PaymentStatusRequest;
import com.intellimarket.api.payments.dto.PaymentsResponse;

import java.util.List;

public interface IOrderService {
    // Gestion del Carrito
    CartResponseDTO getCartByEmail(String email);
    CartResponseDTO addItemToCartByEmail(String email, AddToCartRequestDTO request);
    void clearCartByEmail(String email);

    // Gestion de Ordenes
    // FIX: ahora devuelve una LISTA porque un carrito multi-tienda
    // genera una orden independiente por cada tienda involucrada.
    List<OrderResponseDTO> placeOrderByEmail(String email, OrderRequestDTO request);
    List<OrderResponseDTO> getOrderHistoryByEmail(String email);
    OrderResponseDTO getOrderById(Long orderId);

    // Generar un pago al efectuarse
    PaymentsResponse seePayment(String email, Long orderId);
    PaymentsResponse updatePaymentStatus(String email, Long orderId, PaymentStatusRequest request);
}