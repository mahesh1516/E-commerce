package com.nexora.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String specifications,
        BigDecimal price,
        BigDecimal discountPrice,
        BigDecimal effectivePrice,
        int discountPercent,
        String brand,
        String sku,
        String imageUrl,
        List<String> images,
        Long categoryId,
        String categoryName,
        String categorySlug,
        int stock,
        boolean inStock,
        double rating,
        int reviewCount,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
