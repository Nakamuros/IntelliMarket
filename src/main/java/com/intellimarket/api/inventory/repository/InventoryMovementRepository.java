package com.intellimarket.api.inventory.repository;

import com.intellimarket.api.inventory.model.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

}
