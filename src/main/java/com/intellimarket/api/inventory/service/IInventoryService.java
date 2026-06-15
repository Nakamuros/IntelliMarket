package com.intellimarket.api.inventory.service;

import com.intellimarket.api.inventory.dto.ProductsRequest;
import com.intellimarket.api.inventory.dto.ProductsResponse;

import java.math.BigInteger;
import java.util.List;

public interface IInventoryService {
    // US-05: Creación de nuevo producto
    ProductsResponse createProduct(Long storeId, ProductsRequest product);

    // US-06: Edición de detalle de producto
    ProductsResponse updateProduct(Long productId, Long store_Id, ProductsRequest product);

    // US-07: Listar productos y su stock
    List<ProductsResponse> getStockByStore(Long store_Id);

    // US-08: Alerta de stock crítico en productos (menor a 10 unidades)
    List<ProductsResponse> getCriticalStock(Long store_Id);

    // US-09: Cambiar estado automáticamente en productos cuando se agotan

    // Obtener ProductResponse por su id
    ProductsResponse getProductByIdAndStore(Long productId, Long storeId);
}
