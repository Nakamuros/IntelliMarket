package com.intellimarket.api.inventory.repository;

import com.intellimarket.api.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // US-07: Vendedor visualiza la lista de productos de su tienda con el stock en cuenta
    //List<Inventory> findByUserId(Long user_id);
    // Alternativa usando @Query (JPQL) - Más explícito
    // US-07: Listar solo productos activos (estado 1) de una tienda específica
    // Usando Query Method: Spring entiende que debe filtrar por StoreId Y por State
    List<Inventory> findByStoreIdAndState(Long storeId, Integer state);

    // US-05 es simplemente crear producto, a escribirse en Service

    // US-06 y US-09: Buscar producto específico que esté activo
    Optional<Inventory> findByProductIdAndStoreIdAndState(Long productId, Long storeId, Integer state);

    // US-08: Alerta de stock crítico solo para productos que no han sido "eliminados" (state 1)
    List<Inventory> findByStoreIdAndStockLessThanAndState(Long storeId, Integer stock, Integer state);
}
