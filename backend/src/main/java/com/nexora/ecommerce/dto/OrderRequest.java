package com.nexora.ecommerce.dto;

import com.nexora.ecommerce.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Checkout request. Card details are only needed for CARD payments.
 * SIMULATION: a card number ending in 0000 is declined.
 */
public record OrderRequest(
        @NotNull(message = "Address is required")
        Long addressId,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @Pattern(regexp = "^$|^[0-9]{12,19}$", message = "Card number must be 12-19 digits")
        String cardNumber,

        @Size(max = 100)
        String cardHolder
) {
}
