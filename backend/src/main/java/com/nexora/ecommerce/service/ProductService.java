package com.nexora.ecommerce.service;

import com.nexora.ecommerce.dto.PageResponse;
import com.nexora.ecommerce.dto.ProductFilter;
import com.nexora.ecommerce.dto.ProductRequest;
import com.nexora.ecommerce.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    PageResponse<ProductResponse> getProducts(ProductFilter filter, int page, int size, String sort);

    ProductResponse getProduct(Long id);

    List<String> getBrands();

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);
}
