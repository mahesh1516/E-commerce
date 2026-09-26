package com.nexora.ecommerce.mapper;

import com.nexora.ecommerce.dto.ReviewResponse;
import com.nexora.ecommerce.entity.Review;

public final class ReviewMapper {

    private ReviewMapper() {
    }

    public static ReviewResponse toResponse(Review r) {
        return new ReviewResponse(r.getId(), r.getRating(), r.getComment(),
                r.getUser().getName(), r.getCreatedAt());
    }
}
