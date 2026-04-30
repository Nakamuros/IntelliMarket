package com.intellimarket.api.inventory.repository;

import com.intellimarket.api.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // US-08: Sistema notifica al vendedor si un producto responde a un stock menor a 10 u
    List<Product> findByLessThan(Integer stock);
}
