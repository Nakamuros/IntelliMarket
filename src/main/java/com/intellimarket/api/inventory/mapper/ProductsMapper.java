package com.intellimarket.api.inventory.mapper;

import com.intellimarket.api.inventory.dto.ProductsResponse;
import com.intellimarket.api.inventory.model.Inventory;
import com.intellimarket.api.inventory.model.Products;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductsMapper {
    // US-07, US-08: Unimos el catálogo con la bodega
    @Mapping(target = "name", source = "products.name")
    @Mapping(target = "category", source = "products.category")
    @Mapping(target = "description", source = "products.description")
    @Mapping(target = "stock", source = "inventory.stock")
    @Mapping(target = "price", source = "inventory.price")
    ProductsResponse toResponse(Products product, Inventory inventory);
}
