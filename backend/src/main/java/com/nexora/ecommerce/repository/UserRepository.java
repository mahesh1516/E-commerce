package com.nexora.ecommerce.repository;

import com.nexora.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Derived query: Spring generates "WHERE email = ?"
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
