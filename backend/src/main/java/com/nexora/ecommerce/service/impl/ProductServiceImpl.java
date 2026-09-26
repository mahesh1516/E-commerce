package com.nexora.ecommerce.service.impl;

import com.nexora.ecommerce.dto.PageResponse;
import com.nexora.ecommerce.dto.ProductFilter;
import com.nexora.ecommerce.dto.ProductRequest;
import com.nexora.ecommerce.dto.ProductResponse;
import com.nexora.ecommerce.entity.Category;
import com.nexora.ecommerce.entity.Inventory;
import com.nexora.ecommerce.entity.Product;
import com.nexora.ecommerce.entity.ProductImage;
import com.nexora.ecommerce.exception.DuplicateResourceException;
import com.nexora.ecommerce.exception.InvalidRequestException;
import com.nexora.ecommerce.exception.ResourceNotFoundException;
import com.nexora.ecommerce.mapper.ProductMapper;
import com.nexora.ecommerce.repository.CategoryRepository;
import com.nexora.ecommerce.repository.ProductRepository;
import com.nexora.ecommerce.repository.ProductSpecification;
import com.nexora.ecommerce.service.ProductService;
import com.nexora.ecommerce.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProducts(ProductFilter filter, int page, int size, String sort) {
        if (filter.minPrice() != null && filter.maxPrice() != null
                && filter.minPrice().compareTo(filter.maxPrice()) > 0) {
            throw new InvalidRequestException("minPrice cannot be greater than maxPrice");
        }

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), AppConstants.MAX_PAGE_SIZE);
        PageRequest pageable = PageRequest.of(safePage, safeSize, parseSort(sort));

        Page<ProductResponse> result = productRepository
                .findAll(ProductSpecification.withFilter(filter), pageable)
                .map(ProductMapper::toResponse);

        return PageResponse.from(result);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return ProductMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getBrands() {
        return productRepository.findDistinctBrands();
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        String sku = request.sku().trim().toUpperCase();
        if (productRepository.existsBySku(sku)) {
            throw new DuplicateResourceException("A product with SKU " + sku + " already exists");
        }

        Product product = new Product();
        apply(request, product);

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(request.stock());
        product.setInventory(inventory);

        productRepository.save(product); // cascades inventory + images
        log.info("Product created: id={}, sku={}", product.getId(), product.getSku());
        return ProductMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        String sku = request.sku().trim().toUpperCase();
        if (productRepository.existsBySkuAndIdNot(sku, id)) {
            throw new DuplicateResourceException("A product with SKU " + sku + " already exists");
        }

        apply(request, product);
        product.getInventory().setQuantity(request.stock());

        log.info("Product updated: id={}", id);
        return ProductMapper.toResponse(product);
    }

    /** Soft delete: hide the product but keep it for old orders. */
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        product.setActive(false);
        log.info("Product soft-deleted: id={}", id);
    }

    // ------------------------------------------------------------

    private void apply(ProductRequest r, Product p) {
        if (r.discountPrice() != null && r.discountPrice().compareTo(r.price()) >= 0) {
            throw new InvalidRequestException("Discount price must be lower than price");
        }

        Category category = categoryRepository.findById(r.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", r.categoryId()));

        p.setName(r.name().trim());
        p.setDescription(r.description());
        p.setSpecifications(r.specifications());
        p.setPrice(r.price());
        p.setDiscountPrice(r.discountPrice());
        p.setBrand(r.brand() == null ? null : r.brand().trim());
        p.setSku(r.sku().trim().toUpperCase());
        p.setImageUrl(blankToNull(r.imageUrl()));
        p.setCategory(category);
        p.setActive(true);

        // Replace gallery images (orphanRemoval deletes old rows)
        p.getImages().clear();
        if (r.imageUrls() != null) {
            int order = 0;
            for (String url : r.imageUrls()) {
                if (url == null || url.isBlank()) {
                    continue;
                }
                ProductImage image = new ProductImage();
                image.setProduct(p);
                image.setImageUrl(url.trim());
                image.setSortOrder(order++);
                p.getImages().add(image);
            }
        }
    }

    /** "price,asc" -> Sort.by(ASC, "price"). Only whitelisted fields. */
    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        if (!AppConstants.PRODUCT_SORT_FIELDS.contains(field)) {
            throw new InvalidRequestException("Cannot sort by '" + field + "'. Allowed: "
                    + AppConstants.PRODUCT_SORT_FIELDS);
        }
        Sort.Direction direction = parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        // Secondary sort by id keeps page order stable
        return Sort.by(direction, field).and(Sort.by(Sort.Direction.ASC, "id"));
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
