package com.nexora.ecommerce.dto;

import java.math.BigDecimal;

/** Optional query parameters for GET /api/products. */
public record ProductFilter(
        String search,
        String category,
        String brand,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Boolean discounted
) {
}
