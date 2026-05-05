package com.intellimarket.api.inventory.repository;

import com.intellimarket.api.inventory.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductsRepository extends JpaRepository<Products, Long> {

}
