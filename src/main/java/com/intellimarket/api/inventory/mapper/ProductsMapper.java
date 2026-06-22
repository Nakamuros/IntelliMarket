package com.intellimarket.api.inventory.mapper;

import com.intellimarket.api.inventory.dto.ProductsResponse;
import com.intellimarket.api.inventory.model.Inventory;
import com.intellimarket.api.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductsMapper {
    // US-07, US-08: Unimos el catálogo con la bodega
    @Mapping(target = "id", source = "product.id")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "category", source = "product.category")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "stock", source = "inventory.stock")
    @Mapping(target = "unitPrice", source = "product.unitPrice")
    @Mapping(target = "status", source = "inventory.state")
    @Mapping(target="imageUrl", source="product.imageUrl")
    ProductsResponse toResponse(Product product, Inventory inventory);
}
