package com.nexora.ecommerce.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be 1-5")
        @Max(value = 5, message = "Rating must be 1-5")
        Integer rating,

        @Size(max = 1000, message = "Comment must be at most 1000 characters")
        String comment
) {
}
