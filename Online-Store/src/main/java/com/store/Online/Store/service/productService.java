package com.store.Online.Store.service;

import com.store.Online.Store.dto.ProductRequest;
import com.store.Online.Store.entity.Product;

import java.util.Optional;

public interface productService {

    public  Optional<Product> getProductById(Long productId);

    public void saveProduct(ProductRequest product);

    public void updateProduct(Long id, ProductRequest product);

    public void deleteProduct(Long productId);
}
