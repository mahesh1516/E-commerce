package com.nexora.ecommerce.service;

import com.nexora.ecommerce.dto.CategoryRequest;
import com.nexora.ecommerce.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAll();

    CategoryResponse getById(Long id);

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    void delete(Long id);
}
