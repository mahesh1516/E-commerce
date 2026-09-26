package com.nexora.ecommerce.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        String imageUrl,
        BigDecimal unitPrice,
        BigDecimal finalUnitPrice,
        int quantity,
        BigDecimal lineTotal
) {
}
