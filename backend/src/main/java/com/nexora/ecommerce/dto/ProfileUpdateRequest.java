package com.nexora.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100)
        String name,

        @Pattern(regexp = "^$|^[0-9+\\- ]{7,20}$", message = "Phone number is invalid")
        String phone
) {
}
