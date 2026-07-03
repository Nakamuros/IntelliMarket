package com.intellimarket.api.inventory.repository;

import com.intellimarket.api.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductIdAndStoreId(Long productId, Long storeId);

    Optional<Inventory> findByProductIdAndStoreIdAndState(Long productId, Long storeId, Integer state);

    List<Inventory> findByStoreIdAndState(Long storeId, Integer state);

    List<Inventory> findByStoreIdAndStockLessThan(Long storeId, Integer stock);

    @Query(value = "SELECT i.* FROM inventory i " +
            "JOIN products p ON p.id = i.product_id " +
            "WHERE LOWER(REPLACE(p.name, ' ', '-')) = LOWER(REPLACE(?1, ' ', '-')) " +
            "AND i.store_id = ?2",
            nativeQuery = true)
    Optional<Inventory> findByProductNameAndStoreId(
            String productName,
            Long storeId
    );
}
