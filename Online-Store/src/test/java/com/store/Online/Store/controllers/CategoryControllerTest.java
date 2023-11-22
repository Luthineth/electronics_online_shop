package com.store.Online.Store.controllers;

import com.store.Online.Store.dto.CategoryRequest;
import com.store.Online.Store.entity.Category;
import com.store.Online.Store.exception.*;
import com.store.Online.Store.service.categoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CategoryControllerTest {

    @Mock
    private categoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testGetAllSubCategories_Success() {
        List<Category> subCategories = new ArrayList<>();
        subCategories.add(new Category());
        when(categoryService.getSubCategories()).thenReturn(subCategories);

        ResponseEntity<?> response = categoryController.getAllSubCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subCategories, response.getBody());
    }

    @Test
    void testGetAllSubCategories_CategoryNotFound() {
        when(categoryService.getSubCategories()).thenThrow(new CategoryNotFoundException("Category not found"));

        ResponseEntity<?> response = categoryController.getAllSubCategories();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Category not found", response.getBody());
    }

    @Test
    void testGetAllSubCategories_InternalServerError() {
        when(categoryService.getSubCategories()).thenThrow(new RuntimeException());

        ResponseEntity<?> response = categoryController.getAllSubCategories();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred while retrieving subcategories.", response.getBody());
    }

    @Test
    void testAddCategory_Success() {
        CategoryRequest categoryRequest = new CategoryRequest();
        Category addedCategory = new Category();
        when(categoryService.addCategory(categoryRequest)).thenReturn(addedCategory);

        ResponseEntity<?> response = categoryController.addCategory(categoryRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(addedCategory, response.getBody());
    }

    @Test
    void testAddCategory_NoCategoryName() {
        CategoryRequest categoryRequest = new CategoryRequest();
        when(categoryService.addCategory(categoryRequest)).thenThrow(new NoCategoryName("Missing category name"));

        ResponseEntity<?> response = categoryController.addCategory(categoryRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing category name", response.getBody());
    }

    @Test
    void testAddCategory_CategoryNotFound() {
        CategoryRequest categoryRequest = new CategoryRequest();
        when(categoryService.addCategory(categoryRequest)).thenThrow(new CategoryNotFoundException("Category not found"));

        ResponseEntity<?> response = categoryController.addCategory(categoryRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Category not found", response.getBody());
    }

    @Test
    void testAddCategory_CategoryAdditionException() {
        CategoryRequest categoryRequest = new CategoryRequest();
        when(categoryService.addCategory(categoryRequest)).thenThrow(new CategoryAdditionException("Error adding category"));

        ResponseEntity<?> response = categoryController.addCategory(categoryRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error adding category", response.getBody());
    }

    @Test
    void testUpdateCategory_Success() {
        Long categoryId = 1L;
        CategoryRequest categoryRequest = new CategoryRequest();
        Category updatedCategory = new Category();
        when(categoryService.updateCategory(categoryId, categoryRequest)).thenReturn(updatedCategory);

        ResponseEntity<?> response = categoryController.updateCategory(categoryId, categoryRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCategory, response.getBody());
    }

    @Test
    void testUpdateCategory_NoCategoryName() {
        Long categoryId = 1L;
        CategoryRequest categoryRequest = new CategoryRequest();
        when(categoryService.updateCategory(categoryId, categoryRequest)).thenThrow(new NoCategoryName("Missing category name"));

        ResponseEntity<?> response = categoryController.updateCategory(categoryId, categoryRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing category name", response.getBody());
    }

    @Test
    void testUpdateCategory_CategoryNotFound() {
        Long categoryId = 1L;
        CategoryRequest categoryRequest = new CategoryRequest();
        when(categoryService.updateCategory(categoryId, categoryRequest)).thenThrow(new CategoryNotFoundException("Category not found"));

        ResponseEntity<?> response = categoryController.updateCategory(categoryId, categoryRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Category not found", response.getBody());
    }

    @Test
    void testUpdateCategory_CategoryUpdateException() {
        Long categoryId = 1L;
        CategoryRequest categoryRequest = new CategoryRequest();
        when(categoryService.updateCategory(categoryId, categoryRequest)).thenThrow(new CategoryUpdateException("Error updating category"));

        ResponseEntity<?> response = categoryController.updateCategory(categoryId, categoryRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error updating category", response.getBody());
    }

    @Test
    void testDeleteCategory_Success() {
        Long categoryId = 1L;
        doNothing().when(categoryService).deleteCategory(categoryId);

        ResponseEntity<?> response = categoryController.deleteCategory(categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteCategory_CategoryNotFound() {
        Long categoryId = 1L;
        doThrow(new CategoryNotFoundException("Category not found")).when(categoryService).deleteCategory(categoryId);

        ResponseEntity<?> response = categoryController.deleteCategory(categoryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Category not found", response.getBody());
    }

    @Test
    void testDeleteCategory_SubCategoryNotFound() {
        Long categoryId = 1L;
        doThrow(new SubCategoryNotFoundException("Subcategory not found")).when(categoryService).deleteCategory(categoryId);

        ResponseEntity<?> response = categoryController.deleteCategory(categoryId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Subcategory not found", response.getBody());
    }

    @Test
    void testDeleteCategory_CategoryDeletionException() {
        Long categoryId = 1L;
        doThrow(new CategoryDeletionException("Error deleting category")).when(categoryService).deleteCategory(categoryId);

        ResponseEntity<?> response = categoryController.deleteCategory(categoryId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error deleting category", response.getBody());
    }
}
