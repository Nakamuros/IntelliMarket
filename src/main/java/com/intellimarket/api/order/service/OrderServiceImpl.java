package com.intellimarket.api.order.service;

import com.intellimarket.api.auth.model.User;
import com.intellimarket.api.auth.repository.UserRepository;
import com.intellimarket.api.order.dto.*;
import com.intellimarket.api.order.mapper.OrderMapper;
import com.intellimarket.api.order.model.*;
import com.intellimarket.api.order.repository.*;
import com.intellimarket.api.product.model.Product;
import com.intellimarket.api.product.repository.ProductRepository;
import com.intellimarket.api.store.model.Store;
import com.intellimarket.api.order.service.IOrderService;
import com.intellimarket.api.order.dto.CartResponseDTO;
import com.intellimarket.api.store.repository.StoreRepository;
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

    @Override
    @Transactional(readOnly = true)
    public CartResponseDTO getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
                    return cartRepository.save(Cart.builder().user(user).build());
                });
        // Transformamos y calculamos all en un solo paso
        List<CartItemResponseDTO> items = cart.getItems().stream()
                .map(item -> {
                    BigDecimal subtotal = item.getProduct().getUnitPrice()
                            .multiply(new BigDecimal(item.getQuantity()));
                    return new CartItemResponseDTO(
                            item.getId(),
                            item.getProduct().getName(),
                            item.getProduct().getUnitPrice(),
                            item.getQuantity(),
                            subtotal,
                            item.getProduct().getImage()
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
        //paso 1: obtener el carrito o crear uno si no existe
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId).
                            orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado"));
                    return cartRepository.save(Cart.builder().user(user).build());
                });
        // paso 2: buscar el producto
        Product product = productRepository.findById(request.productId()).
                orElseThrow(()-> new ResourceNotFoundException("Product no encontrado"));
        // paso 3: logica para añadir o actualizar item
        // BUSCAR si el producto ya está en el carrito
        // Recorremos la lista de items del carrito buscando uno que tenga el mismo ID de producto
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // CASO A: El producto YA ESTABA en el carrito
            // Obtenemos el item existente y le sumamos la nueva cantidad
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.quantity());
        } else {
            // CASO B: El producto es NUEVO en el carrito
            // Creamos un nuevo objeto CartItem
            CartItem newItem = CartItem.builder()
                    .cart(cart)      // Lo vinculamos a este carrito
                    .product(product) // Lo vinculamos a este producto
                    .quantity(request.quantity())
                    .build();

            // Lo añadimos a la lista del carrito
            cart.getItems().add(newItem);
        }

        // 4. GUARDAR el carrito
        // Como pusimos "cascade = ALL" en la entidad Cart,
        // al guardar el carrito se guardan/actualizan automáticamente sus items.
        cartRepository.save(cart);

        return getCart(userId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        cart.getItems().clear(); // Esto vacía la lista
        cartRepository.save(cart); // Al guardar, se borran los items de la DB
    }
    @Override
    @Transactional
    public OrderResponseDTO placeOrder(Long userId, OrderRequestDTO request) {
        //1. Buscamos al usuario y su carrito
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("Usuario no encontrado")
        );
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(
                ()-> new ResourceNotFoundException("Carrito no encontrado")
        );
        //2. Verificamos el carrito, no se puede comprar un carrito vacio
        if (cart.getItems().isEmpty()) {
            throw new ResourceNotFoundException("No puedes realizar una orden con el carrito vacio");
        }
        //3. Buscamos la tienda (viene el request)
        Store store = storeRepository.findById(request.storeId()).orElseThrow(
                () -> new ResourceNotFoundException("Tienda no encontrada")
        );

        //4. Creamos la Entidad Orden
        Orders order = Orders.builder()
                .user(user)
                .store(store)
                .status("PENDING")
                .totalAmount(BigDecimal.ZERO)
                .build();
        //5. Converitimos los items del carrito a items de la orden
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    BigDecimal unitPrice = cartItem.getProduct().getUnitPrice();
                    BigDecimal subtotal = unitPrice.multiply(new BigDecimal(cartItem.getQuantity()));
                    
                    return OrderItem.builder()
                            .order(order)
                            .product(cartItem.getProduct())
                            .quantity(cartItem.getQuantity())
                            .unitPrice(unitPrice)
                            .subtotal(subtotal)
                            .build();
                }).collect(Collectors.toList());

        //6. Asignamos los items y calculamos el total final
        order.setItems(orderItems);
        BigDecimal totalOrder = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalOrder);

        //7. Guardamos la orden y vaciamos el carrito
        Orders savedOrder = orderRepository.save(order);
        cart.getItems().clear();
        cartRepository.save(cart);

        //8. devolvemos el DTO usando el Mapper que creamos antes
        return orderMapper.orderToOrderResponseDTO(savedOrder); // Lo programaremos en el siguiente paso
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrderHistory(Long userId) {
        //1. Buscamos todas las ordenes de usuario en la BD
        List<Orders> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        //2. convertimos las lista de Entidades en una lista de DTOs usando el Mapper
        return orders.stream()
                .map(orderMapper::orderToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long orderId) {
        //1. Buscamos la orden por su Id
        Orders order = orderRepository.findById(orderId).
                orElseThrow(() -> new ResourceNotFoundException("Orden no econtrada con ID"+ orderId));
        return orderMapper.orderToOrderResponseDTO(order);
    }

}
