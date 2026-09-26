package com.nexora.ecommerce.repository;

import com.nexora.ecommerce.dto.ProductFilter;
import com.nexora.ecommerce.entity.Category;
import com.nexora.ecommerce.entity.Product;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Builds the WHERE clause for product search from optional filters. */
public final class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> withFilter(ProductFilter f) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Only show active (not soft-deleted) products
            predicates.add(cb.isTrue(root.get("active")));

            if (hasText(f.search())) {
                String like = "%" + f.search().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("brand")), like),
                        cb.like(cb.lower(root.get("description")), like)));
            }

            if (hasText(f.category())) {
                Join<Product, Category> category = root.join("category");
                predicates.add(cb.equal(category.get("slug"), f.category().trim().toLowerCase()));
            }

            if (hasText(f.brand())) {
                predicates.add(cb.equal(cb.lower(root.get("brand")), f.brand().trim().toLowerCase()));
            }

            // Price filters use the price the customer pays:
            // COALESCE(discount_price, price)
            Expression<BigDecimal> effectivePrice =
                    cb.coalesce(root.<BigDecimal>get("discountPrice"), root.<BigDecimal>get("price"));

            if (f.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(effectivePrice, f.minPrice()));
            }
            if (f.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(effectivePrice, f.maxPrice()));
            }

            if (Boolean.TRUE.equals(f.discounted())) {
                predicates.add(cb.isNotNull(root.get("discountPrice")));
                predicates.add(cb.lessThan(root.<BigDecimal>get("discountPrice"), root.<BigDecimal>get("price")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}
