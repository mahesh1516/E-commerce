package com.nexora.ecommerce.service.impl;

import com.nexora.ecommerce.dto.CategoryRequest;
import com.nexora.ecommerce.dto.CategoryResponse;
import com.nexora.ecommerce.entity.Category;
import com.nexora.ecommerce.exception.DuplicateResourceException;
import com.nexora.ecommerce.exception.InvalidRequestException;
import com.nexora.ecommerce.exception.ResourceNotFoundException;
import com.nexora.ecommerce.mapper.CategoryMapper;
import com.nexora.ecommerce.repository.CategoryRepository;
import com.nexora.ecommerce.repository.ProductRepository;
import com.nexora.ecommerce.service.CategoryService;
import com.nexora.ecommerce.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        return CategoryMapper.toResponse(find(id));
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String name = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Category '" + name + "' already exists");
        }
        Category category = new Category();
        category.setName(name);
        category.setSlug(SlugUtil.toSlug(name));
        category.setDescription(request.description());
        categoryRepository.save(category);
        log.info("Category created: id={}", category.getId());
        return CategoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = find(id);
        String name = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new DuplicateResourceException("Category '" + name + "' already exists");
        }
        category.setName(name);
        category.setSlug(SlugUtil.toSlug(name));
        category.setDescription(request.description());
        return CategoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = find(id);
        if (productRepository.existsByCategoryId(id)) {
            throw new InvalidRequestException("Cannot delete a category that still has products");
        }
        categoryRepository.delete(category);
        log.info("Category deleted: id={}", id);
    }

    private Category find(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }
}
