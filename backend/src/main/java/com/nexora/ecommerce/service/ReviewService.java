package com.nexora.ecommerce.service;

import com.nexora.ecommerce.dto.ReviewRequest;
import com.nexora.ecommerce.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse addReview(Long productId, ReviewRequest request);

    List<ReviewResponse> getReviews(Long productId);
}
