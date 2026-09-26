package com.nexora.ecommerce.service;

import com.nexora.ecommerce.dto.ProductResponse;

import java.util.List;

public interface WishlistService {

    List<ProductResponse> getWishlist();

    List<ProductResponse> addProduct(Long productId);

    List<ProductResponse> removeProduct(Long productId);
}
