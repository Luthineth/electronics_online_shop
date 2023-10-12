package com.store.Online.Store.service;

import com.store.Online.Store.dto.ProductRequest;
import com.store.Online.Store.entity.Product;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface productService {

    Optional<Product> getProductById(Long productId);

    ProductRequest getProductRequestById(Long productId, Sort.Direction direction);

    void saveProduct(ProductRequest product);

    void updateProduct(Long id, ProductRequest product);

    void deleteProduct(Long productId);

    List<Product> searchProducts(BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, Integer minRating, List<Product> products);
}
