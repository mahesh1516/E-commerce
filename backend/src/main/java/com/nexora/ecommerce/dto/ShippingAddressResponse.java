package com.nexora.ecommerce.dto;

public record ShippingAddressResponse(
        String name,
        String phone,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String country
) {
}
