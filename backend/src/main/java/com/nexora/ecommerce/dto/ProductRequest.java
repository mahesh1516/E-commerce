package com.nexora.ecommerce.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 200)
        String name,

        @Size(max = 5000)
        String description,

        @Size(max = 5000)
        String specifications,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price,

        @DecimalMin(value = "0.01", message = "Discount price must be greater than 0")
        @Digits(integer = 10, fraction = 2)
        BigDecimal discountPrice,

        @Size(max = 80)
        String brand,

        @NotBlank(message = "SKU is required")
        @Size(max = 60)
        String sku,

        @Size(max = 500)
        String imageUrl,

        /** Extra gallery images (optional). */
        List<@Size(max = 500) String> imageUrls,

        @NotNull(message = "Category is required")
        Long categoryId,

        @NotNull(message = "Stock is required")
        @Min(value = 0, message = "Stock cannot be negative")
        @Max(value = 100000)
        Integer stock
) {
}
