package com.nexora.ecommerce.mapper;

import com.nexora.ecommerce.dto.CategoryResponse;
import com.nexora.ecommerce.entity.Category;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getSlug(), c.getDescription());
    }
}
