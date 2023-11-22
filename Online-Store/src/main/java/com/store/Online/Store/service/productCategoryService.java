package com.store.Online.Store.service;

import com.store.Online.Store.entity.Product;

import java.util.List;
import java.util.Set;

public interface productCategoryService {

    List<Product> getProductsByCategoryAndSubcategories(Long categoryId);

    void getProductsRecursively(Long categoryId, Set<Product> products);
}
