package com.intellimarket.api.order.repository;

import com.intellimarket.api.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Para ver el historial de un cliente específico, ordenado por la más reciente
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    //Boolean existsById(Long id);

    // Para el resumen de ventas del VENDEDOR: trae los pedidos de su tienda
    // con los items y el producto ya cargados (evita N+1 por ser relaciones LAZY).
    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.product " +
            "WHERE o.store.id = :storeId " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByStoreIdWithItems(@Param("storeId") Long storeId);
}