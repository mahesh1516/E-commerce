package com.nexora.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank(message = "Name is required") @Size(max = 100)
        String name,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[0-9+\\- ]{7,20}$", message = "Phone number is invalid")
        String phone,

        @NotBlank(message = "Address line 1 is required") @Size(max = 200)
        String addressLine1,

        @Size(max = 200)
        String addressLine2,

        @NotBlank(message = "City is required") @Size(max = 80)
        String city,

        @NotBlank(message = "State is required") @Size(max = 80)
        String state,

        @NotBlank(message = "Postal code is required")
        @Pattern(regexp = "^[A-Za-z0-9 \\-]{3,10}$", message = "Postal code is invalid")
        String postalCode,

        @NotBlank(message = "Country is required") @Size(max = 80)
        String country,

        Boolean defaultAddress
) {
}
