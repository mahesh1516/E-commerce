package com.nexora.ecommerce.service;

import com.nexora.ecommerce.dto.PageResponse;
import com.nexora.ecommerce.dto.ProductFilter;
import com.nexora.ecommerce.dto.ProductRequest;
import com.nexora.ecommerce.dto.ProductResponse;
import com.nexora.ecommerce.entity.Product;
import com.nexora.ecommerce.exception.DuplicateResourceException;
import com.nexora.ecommerce.exception.InvalidRequestException;
import com.nexora.ecommerce.repository.CategoryRepository;
import com.nexora.ecommerce.repository.ProductRepository;
import com.nexora.ecommerce.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks private ProductServiceImpl productService;

    private ProductRequest request(String price, String discount) {
        return new ProductRequest("Phone", "desc", null, new BigDecimal(price),
                discount == null ? null : new BigDecimal(discount), "Nexora", "tst-1",
                null, List.of(), 1L, 10);
    }

    @Test
    void create_savesProductWithInventory() {
        when(productRepository.existsBySku("TST-1")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(TestData.category()));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductResponse response = productService.createProduct(request("1000", "900"));

        assertThat(response.sku()).isEqualTo("TST-1");       // normalised to upper case
        assertThat(response.stock()).isEqualTo(10);
        assertThat(response.discountPercent()).isEqualTo(10);
        assertThat(response.effectivePrice()).isEqualByComparingTo("900");
    }

    @Test
    void create_duplicateSku_throws() {
        when(productRepository.existsBySku("TST-1")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> productService.createProduct(request("1000", null)));
        verify(productRepository, never()).save(any());
    }

    @Test
    void create_discountNotLowerThanPrice_throws() {
        when(productRepository.existsBySku("TST-1")).thenReturn(false);

        assertThrows(InvalidRequestException.class, () -> productService.createProduct(request("1000", "1000")));
    }

    @Test
    void search_returnsMappedPage() {
        Product product = TestData.product(10L, 5);
        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(product), PageRequest.of(0, 12), 1));

        PageResponse<ProductResponse> page = productService.getProducts(
                new ProductFilter("phone", "mobiles", null, null, null, null), 0, 12, "price,asc");

        assertThat(page.totalElements()).isEqualTo(1);
        assertThat(page.content().get(0).name()).isEqualTo("Test Phone");
    }

    @Test
    void search_invalidSortField_throws() {
        assertThrows(InvalidRequestException.class, () -> productService.getProducts(
                new ProductFilter(null, null, null, null, null, null), 0, 12, "password,asc"));
    }

    @Test
    void search_minGreaterThanMax_throws() {
        assertThrows(InvalidRequestException.class, () -> productService.getProducts(
                new ProductFilter(null, null, null, new BigDecimal("500"), new BigDecimal("100"), null),
                0, 12, null));
    }
}
