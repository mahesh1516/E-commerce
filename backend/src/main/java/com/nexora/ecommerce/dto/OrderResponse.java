package com.nexora.ecommerce.dto;

import com.nexora.ecommerce.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        OrderStatus status,
        boolean cancellable,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal tax,
        BigDecimal shippingFee,
        BigDecimal grandTotal,
        int totalItems,
        ShippingAddressResponse shippingAddress,
        List<OrderItemResponse> items,
        PaymentResponse payment,
        String customerName,
        String customerEmail,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
