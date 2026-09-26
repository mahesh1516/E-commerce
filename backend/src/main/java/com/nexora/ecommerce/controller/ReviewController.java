package com.nexora.ecommerce.controller;

import com.nexora.ecommerce.dto.ReviewRequest;
import com.nexora.ecommerce.dto.ReviewResponse;
import com.nexora.ecommerce.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Reviews for a product (public)")
    public List<ReviewResponse> getReviews(@PathVariable Long productId) {
        return reviewService.getReviews(productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Write a review (logged-in users, one per product)")
    public ReviewResponse addReview(@PathVariable Long productId, @Valid @RequestBody ReviewRequest request) {
        return reviewService.addReview(productId, request);
    }
}
