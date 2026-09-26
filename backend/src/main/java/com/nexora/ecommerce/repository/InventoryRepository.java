package com.nexora.ecommerce.repository;

import com.nexora.ecommerce.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    // JPQL: queries entities/fields, not tables/columns.
    // "join fetch" loads the product in the same SQL query.
    @Query("select i from Inventory i join fetch i.product p "
            + "where p.active = true order by p.name asc")
    List<Inventory> findAllActive();

    @Query("select i from Inventory i join fetch i.product p "
            + "where p.active = true and i.quantity <= i.lowStockThreshold "
            + "order by i.quantity asc")
    List<Inventory> findLowStock();
}
