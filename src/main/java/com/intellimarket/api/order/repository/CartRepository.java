package com.intellimarket.api.order.repository;
import com.intellimarket.api.auth.model.User;
import com.intellimarket.api.order.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    // Este metodo es CLAVE: permite buscar el carrito de un usuario específico
    Optional<Cart> findByUserId(Long userId);

    List<Cart> User(User user);
}
