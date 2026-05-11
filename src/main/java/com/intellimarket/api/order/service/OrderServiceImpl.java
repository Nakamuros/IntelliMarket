package com.intellimarket.api.order.service;

import com.intellimarket.api.auth.model.User;
import com.intellimarket.api.auth.repository.UserRepository;
import com.intellimarket.api.inventory.model.Inventory;
import com.intellimarket.api.inventory.repository.InventoryRepository;
import com.intellimarket.api.order.dto.*;
import com.intellimarket.api.order.mapper.OrderMapper;
import com.intellimarket.api.order.model.*;
import com.intellimarket.api.order.repository.*;
import com.intellimarket.api.product.model.Product;
import com.intellimarket.api.product.repository.ProductRepository;
import com.intellimarket.api.store.model.Store;
import com.intellimarket.api.store.repository.StoreRepository;
import com.intellimarket.api.order.exception.InsufficientStockException;
import com.intellimarket.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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

    @Override
    @Transactional(readOnly = true)
    public CartResponseDTO getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
                    return cartRepository.save(Cart.builder().user(user).build());
                });

        List<CartItemResponseDTO> items = cart.getItems().stream()
                .map(item -> {
                    Inventory inventory = inventoryRepository.findByProductIdAndStoreIdAndState
                                    (item.getProduct().getId(), item.getStore().getId(), 1)
                            .orElseThrow(() -> new ResourceNotFoundException("El producto no está disponible en la tienda " + item.getStore().getName()));

                    BigDecimal subtotal = inventory.getPrice()
                            .multiply(new BigDecimal(item.getQuantity()));
                    return new CartItemResponseDTO(
                            item.getId(),
                            item.getProduct().getName(),
                            inventory.getPrice(),
                            item.getQuantity(),
                            subtotal,
                            item.getProduct().getImage() // Map imageUrl if needed, but CartItemResponseDTO might need update
                    );
                }).toList();

            BigDecimal total = items.stream()
                    .map(CartItemResponseDTO::subtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return new CartResponseDTO(cart.getId(), items, total);
    }


    @Override
    @Transactional
    public CartResponseDTO addItemToCart(Long userId, AddToCartRequestDTO request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId).
                            orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado"));
                    return cartRepository.save(Cart.builder().user(user).build());
                });

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

        return getCart(userId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public OrderResponseDTO placeOrder(Long userId, OrderRequestDTO request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("Usuario no encontrado")
        );
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(
                ()-> new ResourceNotFoundException("Carrito no encontrado")
        );
        
        if (cart.getItems().isEmpty()) {
            throw new ResourceNotFoundException("No puedes realizar una orden con el carrito vacío");
        }

        Store store = storeRepository.findById(request.storeId()).orElseThrow(
                () -> new ResourceNotFoundException("Tienda no encontrada")
        );

        Order order = Order.builder()
                .user(user)
                .store(store)
                .status("PENDING")
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    Inventory inventory = inventoryRepository.findByProductIdAndStoreIdAndState(product.getId(), request.storeId(), 1)
                            .orElseThrow(() -> new ResourceNotFoundException("El producto no está disponible en esta tienda"));
                    
                    if (inventory.getStock() < cartItem.getQuantity()) {
                        throw new InsufficientStockException("Stock insuficiente para el producto: " + product.getName());
                    }

                    inventory.setStock(inventory.getStock() - cartItem.getQuantity());

                    if (inventory.getStock() == 0) {
                        inventory.setState(0);
                    }
                    
                    inventoryRepository.save(inventory);

                    BigDecimal unitPrice = inventory.getPrice(); // Use store-specific price
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
        cart.getItems().clear();
        cartRepository.save(cart);

        return orderMapper.orderToOrderResponseDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrderHistory(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
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

}
