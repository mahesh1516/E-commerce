package com.nexora.ecommerce.controller;

import com.nexora.ecommerce.dto.ProductResponse;
import com.nexora.ecommerce.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist")
@SecurityRequirement(name = "bearerAuth")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "View my wishlist")
    public List<ProductResponse> getWishlist() {
        return wishlistService.getWishlist();
    }

    @PostMapping("/{productId}")
    @Operation(summary = "Add product to wishlist")
    public List<ProductResponse> add(@PathVariable Long productId) {
        return wishlistService.addProduct(productId);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove product from wishlist")
    public List<ProductResponse> remove(@PathVariable Long productId) {
        return wishlistService.removeProduct(productId);
    }
}
