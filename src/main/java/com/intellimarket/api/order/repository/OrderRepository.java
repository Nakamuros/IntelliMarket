package com.intellimarket.api.order.repository;

import com.intellimarket.api.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Para ver el historial de un cliente específico, ordenado por la más reciente
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    //Boolean existsById(Long id);
}
