package com.nexora.ecommerce.dto;

public record LoginResponse(
        String token,
        String tokenType,
        Long userId,
        String username,
        String email,
        String role,          // "USER" or "ADMIN"
        long expiresInMs
) {
}
