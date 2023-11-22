package com.store.Online.Store.service;

import com.store.Online.Store.dto.CategoryRequest;
import com.store.Online.Store.entity.Category;
import com.store.Online.Store.exception.*;
import com.store.Online.Store.repository.categoryRepository;
import com.store.Online.Store.repository.productCategoryRepository;
import com.store.Online.Store.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private categoryRepository categoryRepository;

    @Mock
    private productCategoryRepository productCategoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getSubCategories_ReturnsListOfCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category());
        categories.add(new Category());

        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getSubCategories();

        assertNotNull(result);
        assertEquals(categories.size(), result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void addCategory_ValidCategoryRequest_ReturnsCategory() {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setCategoryName("Test Category");

        when(categoryRepository.save(any(Category.class))).thenReturn(new Category());

        Category result = categoryService.addCategory(categoryRequest);

        assertNotNull(result);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void addCategory_NullCategory_ThrowsCategoryNotFoundException() {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setCategoryName("test");
        categoryRequest.setParentCategoryId(1L);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.addCategory(categoryRequest));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void addCategory_NullCategoryName_ThrowsNoCategoryNameException() {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setCategoryName(null);
        assertThrows(NoCategoryName.class, () -> categoryService.addCategory(categoryRequest));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_ValidCategoryIdAndCategoryRequest_ReturnsUpdatedCategory() {
        Long categoryId = 1L;
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setCategoryName("Updated Category");

        Category existingCategory = new Category();
        existingCategory.setCategoryId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

        Category result = categoryService.updateCategory(categoryId, categoryRequest);

        assertNotNull(result);
        assertEquals(existingCategory, result);
        assertEquals(categoryRequest.getCategoryName(), result.getCategoryName());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_InvalidCategoryId_ThrowsCategoryNotFoundException() {
        Long categoryId = 1L;
        CategoryRequest categoryRequest = new CategoryRequest();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(categoryId, categoryRequest));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_NullCategoryName_ThrowsNoCategoryNameException() {
        Long categoryId = 1L;
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setCategoryName(null);

        Category existingCategory = new Category();
        existingCategory.setCategoryId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));

        assertThrows(NoCategoryName.class, () -> categoryService.updateCategory(categoryId, categoryRequest));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_ValidCategoryId_CategoryDeletedSuccessfully() {
        Long categoryId = 1L;
        Category existingCategory = new Category();
        existingCategory.setCategoryId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findSubCategories(categoryId)).thenReturn(new ArrayList<>());

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).findSubCategories(categoryId);
        verify(productCategoryRepository, times(1)).deleteByCategoryId(existingCategory);
        verify(categoryRepository, times(1)).delete(existingCategory);
    }

    @Test
    void deleteCategory_InvalidCategoryId_ThrowsCategoryNotFoundException() {
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(categoryId));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).findSubCategories(categoryId);
        verify(productCategoryRepository, never()).deleteByCategoryId(any(Category.class));
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void deleteCategory_CategoryWithSubCategories_ThrowsSubCategoryNotFoundException() {
        Long categoryId = 1L;
        Category existingCategory = new Category();
        existingCategory.setCategoryId(categoryId);

        List<Category> subCategories = new ArrayList<>();
        subCategories.add(new Category());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findSubCategories(categoryId)).thenReturn(subCategories);

        assertThrows(SubCategoryNotFoundException.class, () -> categoryService.deleteCategory(categoryId));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).findSubCategories(categoryId);
        verify(productCategoryRepository, never()).deleteByCategoryId(any(Category.class));
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
