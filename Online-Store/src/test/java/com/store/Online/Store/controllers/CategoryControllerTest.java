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

    @Mock
    private Category parentCategory;

    @Mock
    private Category subCategory1;

    @Mock
    private Category subCategory2;

    @Mock
    private  CategoryRequest categoryRequest;
    @BeforeEach
    void setUp() {
        openMocks(this);
        parentCategory = new Category("Аудиотехника", null);
        parentCategory.setCategoryId(1L);

        subCategory1 = new Category("Портативные колонки", parentCategory);
        subCategory1.setCategoryId(8L);

        subCategory2 = new Category("Наушники", parentCategory);
        subCategory2.setCategoryId(9L);

        categoryRequest = new CategoryRequest(null,"Аудиотехника2");
    }

    @Test
    void testGetAllSubCategories_Success_ReturnsHttpStatusOk() {
        List<Category> subCategories = new ArrayList<>();
        subCategories.add(subCategory1);
        subCategories.add(subCategory2);
        when(categoryService.getSubCategories()).thenReturn(subCategories);

        ResponseEntity<?> response = categoryController.getAllSubCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subCategories, response.getBody());
    }

    @Test
    void testGetAllSubCategories_CategoryNotFound_ReturnsHttpStatusNOT_FOUND() {
        when(categoryService.getSubCategories()).thenThrow(new CategoryNotFoundException("Category not found"));

        ResponseEntity<?> response = categoryController.getAllSubCategories();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Category not found", response.getBody());
    }

    @Test
    void testGetAllSubCategories_InternalServerError_ReturnsHttpStatusINTERNAL_SERVER_ERROR() {
        when(categoryService.getSubCategories()).thenThrow(new RuntimeException());

        ResponseEntity<?> response = categoryController.getAllSubCategories();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred while retrieving subcategories.", response.getBody());
    }

    @Test
    void testAddCategory_Success_ReturnsHttpStatusCREATED() {
        when(categoryService.addCategory(categoryRequest)).thenReturn(parentCategory);

        ResponseEntity<?> response = categoryController.addCategory(categoryRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(parentCategory, response.getBody());
    }

    @Test
    void testAddCategory_NoCategoryName_ReturnsHttpStatusBAD_REQUEST() {
        when(categoryService.addCategory(categoryRequest)).thenThrow(new NoCategoryName("Missing category name"));

        ResponseEntity<?> response = categoryController.addCategory(categoryRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing category name", response.getBody());
    }

    @Test
    void testAddCategory_CategoryNotFound_ReturnsHttpStatusNOT_FOUND() {
        when(categoryService.addCategory(categoryRequest)).thenThrow(new CategoryNotFoundException("Category not found"));

        ResponseEntity<?> response = categoryController.addCategory(categoryRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Category not found", response.getBody());
    }

    @Test
    void testAddCategory_CategoryAdditionException_ReturnsHttpStatusINTERNAL_SERVER_ERROR() {
        when(categoryService.addCategory(categoryRequest)).thenThrow(new CategoryAdditionException("Error adding category"));

        ResponseEntity<?> response = categoryController.addCategory(categoryRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error adding category", response.getBody());
    }

    @Test
    void testUpdateCategory_Success_ReturnsHttpStatusOk() {
        Category updatedCategory = new Category("Аудиотехника2", null);
        when(categoryService.updateCategory(parentCategory.getCategoryId(), categoryRequest)).thenReturn(updatedCategory);

        ResponseEntity<?> response = categoryController.updateCategory(parentCategory.getCategoryId(), categoryRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCategory, response.getBody());
    }

    @Test
    void testUpdateCategory_NoCategoryName_ReturnsHttpStatusBAD_REQUEST() {
        when(categoryService.updateCategory(parentCategory.getCategoryId(), categoryRequest)).thenThrow(new NoCategoryName("Missing category name"));

        ResponseEntity<?> response = categoryController.updateCategory(parentCategory.getCategoryId(), categoryRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing category name", response.getBody());
    }

    @Test
    void testUpdateCategory_CategoryNotFoundReturnsHttpStatusNOT_FOUND() {
        when(categoryService.updateCategory(parentCategory.getCategoryId(), categoryRequest)).thenThrow(new CategoryNotFoundException("Category not found"));

        ResponseEntity<?> response = categoryController.updateCategory(parentCategory.getCategoryId(), categoryRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Category not found", response.getBody());
    }

    @Test
    void testUpdateCategory_CategoryUpdateExceptionReturnsHttpStatusINTERNAL_SERVER_ERROR() {
        when(categoryService.updateCategory(parentCategory.getCategoryId(), categoryRequest)).thenThrow(new CategoryUpdateException("Error updating category"));

        ResponseEntity<?> response = categoryController.updateCategory(parentCategory.getCategoryId(), categoryRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error updating category", response.getBody());
    }

    @Test
    void testDeleteCategory_Success_ReturnsHttpStatusOk() {
        doNothing().when(categoryService).deleteCategory(parentCategory.getCategoryId());

        ResponseEntity<?> response = categoryController.deleteCategory(parentCategory.getCategoryId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteCategory_CategoryNotFound_ReturnsHttpStatusNOT_FOUND() {
        doThrow(new CategoryNotFoundException("Category not found")).when(categoryService).deleteCategory(parentCategory.getCategoryId());

        ResponseEntity<?> response = categoryController.deleteCategory(parentCategory.getCategoryId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Category not found", response.getBody());
    }

    @Test
    void testDeleteCategory_SubCategoryNotFound_ReturnsHttpStatusBAD_REQUEST() {
        doThrow(new SubCategoryNotFoundException("Subcategory not found")).when(categoryService).deleteCategory(parentCategory.getCategoryId());

        ResponseEntity<?> response = categoryController.deleteCategory(parentCategory.getCategoryId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Subcategory not found", response.getBody());
    }

    @Test
    void testDeleteCategory_CategoryDeletionException_ReturnsHttpStatusINTERNAL_SERVER_ERROR() {
        doThrow(new CategoryDeletionException("Error deleting category")).when(categoryService).deleteCategory(parentCategory.getCategoryId());

        ResponseEntity<?> response = categoryController.deleteCategory(parentCategory.getCategoryId());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error deleting category", response.getBody());
    }
}
