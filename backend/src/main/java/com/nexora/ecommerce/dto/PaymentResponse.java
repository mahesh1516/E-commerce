package com.nexora.ecommerce.dto;

import com.nexora.ecommerce.entity.PaymentMethod;
import com.nexora.ecommerce.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        BigDecimal amount,
        PaymentMethod method,
        PaymentStatus status,
        String transactionId,
        String cardLast4,
        LocalDateTime createdAt
) {
}
