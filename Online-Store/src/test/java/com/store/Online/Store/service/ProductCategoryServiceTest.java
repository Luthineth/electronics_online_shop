package com.store.Online.Store.service;

import com.store.Online.Store.entity.Category;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.exception.CategoryNotFoundException;
import com.store.Online.Store.repository.categoryRepository;
import com.store.Online.Store.repository.productCategoryRepository;
import com.store.Online.Store.service.impl.ProductCategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class ProductCategoryServiceTest {

    @Mock
    private productCategoryRepository productCategoryRepository;

    @Mock
    private categoryRepository categoryRepository;

    @InjectMocks
    private ProductCategoryServiceImpl productCategoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getProductsByCategoryAndSubcategories_CategoryNotFound_ThrowsCategoryNotFoundException() {
        Long categoryId = 1L;
        when(categoryRepository.existsById(categoryId)).thenReturn(false);
        assertThrows(CategoryNotFoundException.class, () -> productCategoryService.getProductsByCategoryAndSubcategories(categoryId));
    }

    @Test
    void getProductsByCategoryAndSubcategories_CategoryFound_ReturnsProductList() {
        Long categoryId = 1L;
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        List<Product> expectedProducts = new ArrayList<>();
        when(productCategoryRepository.findByCategoryId(anyLong())).thenReturn(expectedProducts);
        List<Product> result = productCategoryService.getProductsByCategoryAndSubcategories(categoryId);
        assertEquals(expectedProducts, result);
    }

    @Test
    void getProductsRecursively_SubcategoriesExist_ReturnsMergedProductList() {
        Long categoryId = 1L;
        List<Category> subcategories = new ArrayList<>();
        Category subcategory = new Category("Subcategory", null);
        subcategory.setCategoryId(2L);
        subcategories.add(subcategory);

        when(categoryRepository.findSubCategories(categoryId)).thenReturn(subcategories);

        List<Product> productsForCategory = new ArrayList<>();
        when(productCategoryRepository.findByCategoryId(categoryId)).thenReturn(productsForCategory);

        List<Product> productsForSubcategory = new ArrayList<>();
        when(productCategoryRepository.findByCategoryId(2L)).thenReturn(productsForSubcategory);

        Set<Product> expectedProducts = new HashSet<>();
        expectedProducts.addAll(productsForCategory);
        expectedProducts.addAll(productsForSubcategory);

        Set<Product> result = new HashSet<>();
        productCategoryService.getProductsRecursively(categoryId, result);
        assertEquals(expectedProducts, result);
    }
}
