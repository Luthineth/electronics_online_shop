package com.store.Online.Store.service;

import com.store.Online.Store.dto.ProductRequest;
import com.store.Online.Store.entity.Product;
import lombok.SneakyThrows;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface productService {

    Optional<Product> getProductById(Long productId);

    ProductRequest getProductRequestById(Long productId, Sort.Direction direction);

    Product addProduct(ProductRequest product, MultipartFile file);

    Product updateProduct(Long id, ProductRequest product, MultipartFile file);

    void deleteProduct(Long productId);

    List<Product> searchProducts(BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, Integer minRating, Sort.Direction price, List<Product> products);

    @SneakyThrows
    byte[] getFile(String fileName);
}
