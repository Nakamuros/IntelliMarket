package com.intellimarket.api.order.service;

import com.intellimarket.api.auth.model.User;
import com.intellimarket.api.auth.repository.UserRepository;
import com.intellimarket.api.inventory.model.Inventory;
import com.intellimarket.api.inventory.repository.InventoryRepository;
import com.intellimarket.api.order.dto.*;
import com.intellimarket.api.order.mapper.OrderMapper;
import com.intellimarket.api.order.model.*;
import com.intellimarket.api.order.repository.*;
import com.intellimarket.api.payments.dto.PaymentStatusRequest;
import com.intellimarket.api.payments.dto.PaymentsResponse;
import com.intellimarket.api.payments.mapper.PaymentsMapper;
import com.intellimarket.api.payments.model.Method;
import com.intellimarket.api.payments.model.PaymentStatus;
import com.intellimarket.api.payments.model.Payments;
import com.intellimarket.api.payments.repository.PaymentsRepository;
import com.intellimarket.api.product.model.Product;
import com.intellimarket.api.product.repository.ProductRepository;
import com.intellimarket.api.store.model.Store;
import com.intellimarket.api.store.repository.StoreRepository;
import com.intellimarket.api.order.exception.InsufficientStockException;
import com.intellimarket.api.shared.exception.BusinessRuleException;
import com.intellimarket.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final InventoryRepository inventoryRepository;

    private final PaymentsRepository paymentsRepository;
    private final PaymentsMapper paymentsMapper;

    @Override
    @Transactional
    public CartResponseDTO getCartByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));

        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponseDTO addItemToCartByEmail(String email, AddToCartRequestDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));

        Product product = productRepository.findById(request.productId()).
                orElseThrow(()-> new ResourceNotFoundException("Producto no encontrado"));

        Store store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tienda no encontrada"));

        Inventory inventory = inventoryRepository.findByProductIdAndStoreIdAndState(product.getId(), request.storeId(), 1)
                .orElseThrow(() -> new ResourceNotFoundException("El producto no está disponible en esta tienda"));

        if (inventory.getStock() <= 0) {
            throw new InsufficientStockException("El producto " + product.getName() + " está agotado.");
        }

        if (inventory.getStock() < request.quantity()) {
            throw new InsufficientStockException("No hay suficiente stock disponible. Stock actual: " + inventory.getStock());
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()) && item.getStore().getId().equals(store.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.quantity();

            if (newQuantity > inventory.getStock()) {
                throw new InsufficientStockException("La cantidad total en el carrito supera el stock disponible.");
            }
            item.setQuantity(newQuantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .store(store)
                    .quantity(request.quantity())
                    .build();

            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);

        return getCartByEmail(email);
    }

    // NUEVO: actualiza solo la cantidad de un CartItem ya existente.
    // El itemId es el id propio del CartItem (item.getId() en CartItemResponseDTO),
    // por lo que el frontend NO necesita conocer productId ni storeId para esta operación.
    @Override
    @Transactional
    public CartResponseDTO updateCartItemQuantity(String email, Long itemId, UpdateCartItemRequestDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("El producto no se encuentra en tu carrito"));

        // Validamos contra el stock real de la tienda asociada a ESE item específico
        Inventory inventory = inventoryRepository.findByProductIdAndStoreIdAndState(
                        item.getProduct().getId(), item.getStore().getId(), 1)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El producto ya no está disponible en la tienda " + item.getStore().getName()));

        if (request.quantity() > inventory.getStock()) {
            throw new InsufficientStockException(
                    "Solo hay " + inventory.getStock() + " unidades disponibles de " + item.getProduct().getName());
        }

        item.setQuantity(request.quantity());
        cartRepository.save(cart);

        return getCartByEmail(email);
    }

    // NUEVO: elimina un item del carrito por su propio id.
    // El frontend (cart.service.ts) ya estaba llamando a este endpoint,
    // pero no existía la implementación en el backend.
    @Override
    @Transactional
    public CartResponseDTO removeItemFromCart(String email, Long itemId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));

        if (!removed) {
            throw new ResourceNotFoundException("El producto no se encuentra en tu carrito");
        }

        cartRepository.save(cart);

        return getCartByEmail(email);
    }

    @Override
    @Transactional
    public void clearCartByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public List<OrderResponseDTO> placeOrderByEmail(String email, OrderRequestDTO request) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Usuario no encontrado")
        );
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Carrito no encontrado")
        );

        if (cart.getItems().isEmpty()) {
            throw new ResourceNotFoundException("No puedes realizar una orden con el carrito vacío");
        }

        Map<Long, List<CartItem>> itemsByStore = cart.getItems().stream()
                .collect(Collectors.groupingBy(item -> item.getStore().getId()));

        List<Order> createdOrders = new ArrayList<>();

        for (Map.Entry<Long, List<CartItem>> entry : itemsByStore.entrySet()) {
            Long storeId = entry.getKey();
            List<CartItem> storeItems = entry.getValue();

            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tienda no encontrada"));

            Order order = Order.builder()
                    .user(user)
                    .store(store)
                    .status("PENDING")
                    .totalAmount(BigDecimal.ZERO)
                    .build();

            List<OrderItem> orderItems = storeItems.stream()
                    .map(cartItem -> {
                        Product product = cartItem.getProduct();
                        Inventory inventory = inventoryRepository.findByProductIdAndStoreIdAndState(product.getId(), storeId, 1)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "El producto " + product.getName() + " no está disponible en la tienda " + store.getName()));

                        if (inventory.getStock() <= 0) {
                            throw new InsufficientStockException("El producto " + product.getName() + " está agotado.");
                        }

                        if (inventory.getStock() < cartItem.getQuantity()) {
                            throw new InsufficientStockException("Stock insuficiente para el producto: " + product.getName());
                        }

                        inventory.setStock(inventory.getStock() - cartItem.getQuantity());
                        inventoryRepository.save(inventory);

                        BigDecimal unitPrice = inventory.getPrice();
                        BigDecimal subtotal = unitPrice.multiply(new BigDecimal(cartItem.getQuantity()));

                        return OrderItem.builder()
                                .order(order)
                                .product(product)
                                .quantity(cartItem.getQuantity())
                                .unitPrice(unitPrice)
                                .subtotal(subtotal)
                                .build();
                    }).collect(Collectors.toList());

            order.setItems(orderItems);
            BigDecimal totalOrder = orderItems.stream()
                    .map(OrderItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalAmount(totalOrder);

            Order savedOrder = orderRepository.save(order);

            Payments payments = Payments.builder()
                    .order(savedOrder)
                    .externalReference("PAY-" + UUID.randomUUID())
                    .method(Method.CARD)
                    .status(PaymentStatus.PENDIENT.name())
                    .amount(totalOrder)
                    .build();
            paymentsRepository.save(payments);

            createdOrders.add(savedOrder);
        }

        cart.getItems().clear();
        cartRepository.save(cart);

        return createdOrders.stream()
                .map(orderMapper::orderToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentsResponse seePayment(String email, Long orderId) {
        getOwnedOrder(email, orderId);

        Payments payments = paymentsRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ningún pago asociado a la orden: " + orderId));

        return paymentsMapper.toResponse(payments);
    }

    @Override
    @Transactional
    public PaymentsResponse updatePaymentStatus(String email, Long orderId, PaymentStatusRequest request) {
        Order order = getOwnedOrder(email, orderId);

        Payments payments = paymentsRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ningún pago asociado a la orden: " + orderId));

        if (!PaymentStatus.PENDIENT.name().equals(payments.getStatus())) {
            throw new BusinessRuleException("El pago ya fue procesado.");
        }

        if (request.status() == PaymentStatus.PENDIENT) {
            throw new BusinessRuleException("El pago no puede volver a estado PENDIENT.");
        }

        payments.setStatus(request.status().name());

        if (request.status() == PaymentStatus.COMPLETED) {
            order.setStatus("COMPLETED");
        } else if (request.status() == PaymentStatus.CANCELLED) {
            order.setStatus("FAILED");
            restoreInventoryForCancelledPayment(order);
        }

        orderRepository.save(order);
        Payments savedPayments = paymentsRepository.save(payments);
        return paymentsMapper.toResponse(savedPayments);
    }

    private Order getOwnedOrder(String email, Long orderId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Orden no encontrada con ID: " + orderId);
        }

        return order;
    }

    private void restoreInventoryForCancelledPayment(Order order) {
        for (OrderItem item : order.getItems()) {
            Inventory inventory = inventoryRepository.findByProductIdAndStoreId(
                    item.getProduct().getId(),
                    order.getStore().getId()
            ).orElseThrow(() -> new ResourceNotFoundException(
                    "Inventario no encontrado para el producto: " + item.getProduct().getName()
            ));

            inventory.setStock(inventory.getStock() + item.getQuantity());
            inventoryRepository.save(inventory);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrderHistoryByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return orders.stream()
                .map(orderMapper::orderToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).
                orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + orderId));
        return orderMapper.orderToOrderResponseDTO(order);
    }

    // Helper interno para construir el DTO de respuesta del carrito, reutilizado
    // por getCartByEmail / updateCartItemQuantity / removeItemFromCart
    private CartResponseDTO buildCartResponse(Cart cart) {
        List<CartItemResponseDTO> items = cart.getItems().stream()
                .map(item -> {
                    Inventory inventory = inventoryRepository.findByProductIdAndStoreId
                                    (item.getProduct().getId(), item.getStore().getId())
                            .orElseThrow(() -> new ResourceNotFoundException("El producto no está disponible en la tienda " + item.getStore().getName()));

                    BigDecimal subtotal = inventory.getPrice()
                            .multiply(new BigDecimal(item.getQuantity()));
                    return new CartItemResponseDTO(
                            item.getId(),
                            item.getProduct().getName(),
                            inventory.getPrice(),
                            item.getQuantity(),
                            subtotal,
                            item.getProduct().getImageUrl()
                    );
                }).toList();

        BigDecimal total = items.stream()
                .map(CartItemResponseDTO::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponseDTO(cart.getId(), items, total);
    }
}