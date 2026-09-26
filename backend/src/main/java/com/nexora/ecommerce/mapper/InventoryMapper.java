package com.nexora.ecommerce.mapper;

import com.nexora.ecommerce.dto.InventoryResponse;
import com.nexora.ecommerce.entity.Inventory;

public final class InventoryMapper {

    private InventoryMapper() {
    }

    public static InventoryResponse toResponse(Inventory i) {
        return new InventoryResponse(
                i.getProduct().getId(),
                i.getProduct().getName(),
                i.getProduct().getSku(),
                i.getQuantity(),
                i.getLowStockThreshold(),
                i.getQuantity() <= i.getLowStockThreshold());
    }
}
