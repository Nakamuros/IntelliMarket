package com.intellimarket.api.order.mapper;

import com.intellimarket.api.order.dto.CartItemResponseDTO;
import com.intellimarket.api.order.dto.CartResponseDTO;
import com.intellimarket.api.order.dto.OrderItemResponseDTO;
import com.intellimarket.api.order.dto.OrderResponseDTO;
import com.intellimarket.api.order.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "order.store.id", target = "storeId")
    @Mapping(source = "order.store.name", target = "storeName")
    OrderResponseDTO orderToOrderResponseDTO(Orders order);

    @Mapping(source = "orderItem.product.name", target = "productName")
    @Mapping(source = "orderItem.product.image", target = "imageUrl")
    OrderItemResponseDTO orderItemToOrderItemResponseDTO(OrderItem orderItem);

    @Mapping(target = "total", ignore = true)
    CartResponseDTO cartToCartResponseDTO(Cart cart);

    @Mapping(source = "cartItem.product.name", target = "productName")
    @Mapping(source = "cartItem.product.image", target = "imageUrl")
    @Mapping(source = "cartItem.product.unitPrice", target = "unitPrice")
    @Mapping(target = "subtotal", ignore = true)
    CartItemResponseDTO cartItemToCartItemResponseDTO(CartItem cartItem);

}