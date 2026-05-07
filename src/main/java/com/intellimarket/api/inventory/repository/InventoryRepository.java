package com.intellimarket.api.inventory.repository;

import com.intellimarket.api.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // US-07: Vendedor visualiza la lista de productos de su tienda con el stock en cuenta
    //List<Inventory> findByUserId(Long user_id);
    // No entiendo nada
    List<Inventory> findByStoreId(Long storeId);
    // Imagine all the people, living for today

    // US-05 es simplemente crear producto, a escribirse en Service

    // US-06: Edición de información de un producto. Buscar producto por ID de store y producto
    // Buscamos el producto en específico de acuerdo al su ID y al de la tienda
    // US-09 orientada con esta función, especificada en Service
    Optional<Inventory> findByProductIdAndStoreId(Long productId, Long storeId);
    //Optional<Inventory> findByProductIdAndUserId(Long product_id, Long user_id);
    // US-08: Alerta de stock de aquellos productos con menos de 10 unidades en la tienda
    //List<Inventory> findByUserIdAndStockLessThan(Long user_id, Integer stock);
    List<Inventory> findByStoreIdAndStockLessThan(Long storeId, Integer stock);
}
