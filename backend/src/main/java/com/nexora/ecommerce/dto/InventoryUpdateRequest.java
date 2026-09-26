package com.nexora.ecommerce.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryUpdateRequest(
        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        @Max(100000)
        Integer quantity,

        @Min(value = 0, message = "Threshold cannot be negative")
        Integer lowStockThreshold
) {
}
