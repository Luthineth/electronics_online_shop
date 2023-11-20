package com.store.Online.Store.service.impl;

import com.store.Online.Store.entity.Category;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.exception.CategoryNotFoundException;
import com.store.Online.Store.exception.ProductNotFoundException;
import com.store.Online.Store.service.productCategoryService;
import com.store.Online.Store.repository.productCategoryRepository;
import com.store.Online.Store.repository.categoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException("Category with ID " + categoryId + " not found");
        }

        List<Product> result = productCategoryRepository.findByCategoryId(categoryId);

        Set<Product> products = new HashSet<>();

        getProductsRecursively(categoryId, products);

        result.addAll(products);
        return result;
    }

    @Override
    public void getProductsRecursively(Long categoryId, Set<Product> products) {
        List<Category> subcategories = categoryRepository.findSubCategories(categoryId);

        List<Product> productsForCategory = productCategoryRepository.findByCategoryId(categoryId);
        products.addAll(productsForCategory);

        for (Category subcategory : subcategories) {
            getProductsRecursively(subcategory.getCategoryId(), products);
        }
    }
}
