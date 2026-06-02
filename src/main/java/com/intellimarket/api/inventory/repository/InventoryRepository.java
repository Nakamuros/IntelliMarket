package com.intellimarket.api.inventory.repository;

import com.intellimarket.api.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByStoreIdAndState(Long storeId, Integer state);

    Optional<Inventory> findByProductIdAndStoreIdAndState(Long productId, Long storeId, Integer state);

    Optional<Inventory> findByProductIdAndStoreId(Long productId, Long storeId);

    List<Inventory> findByStoreIdAndStockLessThanAndState(Long storeId, Integer stock, Integer state);
}
