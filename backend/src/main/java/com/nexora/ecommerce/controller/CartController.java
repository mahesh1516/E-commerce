package com.nexora.ecommerce.controller;

import com.nexora.ecommerce.dto.CartItemRequest;
import com.nexora.ecommerce.dto.CartResponse;
import com.nexora.ecommerce.dto.UpdateQuantityRequest;
import com.nexora.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "View my cart with totals")
    public CartResponse getCart() {
        return cartService.getCart();
    }

    @PostMapping("/items")
    @Operation(summary = "Add a product (adds to quantity if already in cart)")
    public CartResponse addItem(@Valid @RequestBody CartItemRequest request) {
        return cartService.addItem(request);
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "Change quantity of a cart item")
    public CartResponse updateItem(@PathVariable Long id, @Valid @RequestBody UpdateQuantityRequest request) {
        return cartService.updateItem(id, request.quantity());
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Remove a cart item")
    public CartResponse removeItem(@PathVariable Long id) {
        return cartService.removeItem(id);
    }

    @DeleteMapping
    @Operation(summary = "Clear the cart")
    public CartResponse clear() {
        return cartService.clearCart();
    }
}
