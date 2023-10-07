package com.store.Online.Store.service;

import com.store.Online.Store.entity.Product;

import java.util.List;

public interface productCategoryService {
    List<Product> getProductsByCategoryAndSubcategories(Long categoryId);

    List<Product> getProductsRecursively(Long categoryId, List<Product> products);
}
