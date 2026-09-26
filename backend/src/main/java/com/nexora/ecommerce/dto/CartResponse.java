package com.nexora.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        List<CartItemResponse> items,
        int totalItems,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal tax,
        BigDecimal shipping,
        BigDecimal grandTotal
) {
}
