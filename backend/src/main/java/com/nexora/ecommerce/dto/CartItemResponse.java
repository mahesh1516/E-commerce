package com.nexora.ecommerce.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        String brand,
        String imageUrl,
        BigDecimal price,       // list price per unit
        BigDecimal unitPrice,   // price paid per unit
        int quantity,
        BigDecimal lineTotal,
        int availableStock
) {
}
