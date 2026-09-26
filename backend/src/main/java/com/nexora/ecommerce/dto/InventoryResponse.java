package com.nexora.ecommerce.dto;

public record InventoryResponse(
        Long productId,
        String productName,
        String sku,
        int quantity,
        int lowStockThreshold,
        boolean lowStock
) {
}
