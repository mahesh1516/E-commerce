package com.nexora.ecommerce.controller;

import com.nexora.ecommerce.dto.*;
import com.nexora.ecommerce.service.ProductService;
import com.nexora.ecommerce.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Browse (public) and manage (admin) products")
public class ProductController {

    private final ProductService productService;

    /**
     * Examples:
     * /api/products?page=0&size=10
     * /api/products?search=iphone
     * /api/products?category=electronics&brand=samsung
     * /api/products?minPrice=100&maxPrice=1000&sort=price,asc
     */
    @GetMapping
    @Operation(summary = "Search, filter, sort and paginate products")
    public PageResponse<ProductResponse> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean discounted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        ProductFilter filter = new ProductFilter(search, category, brand, minPrice, maxPrice, discounted);
        return productService.getProducts(filter, page, size, sort);
    }

    @GetMapping("/brands")
    @Operation(summary = "List all brands (for the brand filter)")
    public List<String> getBrands() {
        return productService.getBrands();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one product")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a product (ADMIN)")
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a product (ADMIN)")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Soft-delete a product (ADMIN)")
    public ApiResponse delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.ok("Product deleted");
    }
}
