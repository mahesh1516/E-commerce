package com.nexora.ecommerce.repository;

import com.nexora.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * JpaSpecificationExecutor lets us build dynamic WHERE clauses
 * (search + filters) - see ProductSpecification.
 */
public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    boolean existsByCategoryId(Long categoryId);

    @Query("select distinct p.brand from Product p "
            + "where p.active = true and p.brand is not null order by p.brand")
    List<String> findDistinctBrands();

    long countByActiveTrue();
}
