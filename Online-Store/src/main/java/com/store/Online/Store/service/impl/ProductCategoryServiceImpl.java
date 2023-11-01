package com.store.Online.Store.service.impl;

import com.store.Online.Store.entity.Category;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.exception.ProductNotFoundException;
import com.store.Online.Store.service.productCategoryService;
import com.store.Online.Store.repository.productCategoryRepository;
import com.store.Online.Store.repository.categoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductCategoryServiceImpl implements productCategoryService {

    private final productCategoryRepository productCategoryRepository;

    private final categoryRepository categoryRepository;

    @Autowired
    public ProductCategoryServiceImpl(productCategoryRepository productCategoryRepository, categoryRepository categoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Product> getProductsByCategoryAndSubcategories(Long categoryId) {
        List<Product> products = productCategoryRepository.findByCategoryId(categoryId);

        return getProductsRecursively(categoryId, products);
    }

    @Override
    public List<Product> getProductsRecursively(Long categoryId, List<Product> products) {
        List<Product> allProducts = new ArrayList<>(products);

        List<Category> subcategories = categoryRepository.findSubCategories(categoryId);
        for (Category subcategory : subcategories) {
            List<Product> productsForSubcategory = productCategoryRepository.findByCategoryId(subcategory.getCategoryId());
            allProducts.addAll(getProductsRecursively(subcategory.getCategoryId(), productsForSubcategory));
        }

        return allProducts;
    }
}
