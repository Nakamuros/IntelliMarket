package com.intellimarket.api.inventory.repository;

import com.intellimarket.api.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // US-07: Vendedor visualiza la lista de productos de su tienda con el stock en cuenta
    List<Inventory> findByStoreId(Long store_id);

    // US-05 es simplemente crear producto, a escribirse en Service

    // US-06: Edición de información de un producto. Buscar producto por ID de store y producto
    // Buscamos el producto en específico de acuerdo al su ID y al de la tienda
    // US-09 orientada con esta función, especificada en Service
    Optional<Inventory> findByProductIdAndStoreId(Long product_id, Long store_id);

    // US-08: Alerta de stock de aquellos productos con menos de 10 unidades en la tienda
    List<Inventory> findByStoreIdAndStockLessThan(Long store_id, Integer stock);
}
