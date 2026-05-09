package com.intellimarket.api.inventory.repository;

import com.intellimarket.api.inventory.model.Category;
import com.intellimarket.api.inventory.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface ProductsRepository extends JpaRepository<Products, Long> {
    // US-06: Edición de detalle y características de un producto
    // El vendedor revisa y edita cualquier ítem de ProductsResponse

    // US-09: Eliminar producto en específico

    // ¿Qué necesitamos? ID de producto, antes ID
}
