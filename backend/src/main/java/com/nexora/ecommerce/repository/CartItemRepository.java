package com.nexora.ecommerce.repository;

import com.nexora.ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Nested property path: cartItem.cart.user.id
    Optional<CartItem> findByIdAndCartUserId(Long id, Long userId);
}
