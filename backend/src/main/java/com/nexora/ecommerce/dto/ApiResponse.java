package com.nexora.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Consistent envelope for errors and simple messages.
 * Example: { "success": false, "message": "Product not found",
 *            "status": 404, "timestamp": "..." }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse(
        boolean success,
        String message,
        int status,
        LocalDateTime timestamp,
        Map<String, String> errors
) {
    public static ApiResponse ok(String message) {
        return new ApiResponse(true, message, 200, LocalDateTime.now(), null);
    }

    public static ApiResponse error(int status, String message) {
        return new ApiResponse(false, message, status, LocalDateTime.now(), null);
    }

    public static ApiResponse validation(Map<String, String> errors) {
        return new ApiResponse(false, "Validation failed", 400, LocalDateTime.now(), errors);
    }
}
