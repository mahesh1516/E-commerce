package com.nexora.ecommerce.exception;

import com.nexora.ecommerce.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void notFound_returns404WithConsistentBody() {
        ResponseEntity<ApiResponse> response =
                handler.handleNotFound(new ResourceNotFoundException("Product not found"));

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isFalse();
        assertThat(response.getBody().message()).isEqualTo("Product not found");
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void insufficientStock_returns409() {
        ResponseEntity<ApiResponse> response =
                handler.handleStock(new InsufficientStockException("Only 1 left"));

        assertThat(response.getStatusCode().value()).isEqualTo(409);
    }

    @Test
    void duplicate_returns409() {
        ResponseEntity<ApiResponse> response =
                handler.handleDuplicate(new DuplicateResourceException("Email is already registered"));

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody().message()).contains("already registered");
    }
}
