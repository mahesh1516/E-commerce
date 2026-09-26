package com.nexora.ecommerce.dto;

import java.math.BigDecimal;

public record PriceBreakdown(
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal tax,
        BigDecimal shipping,
        BigDecimal grandTotal
) {
}
