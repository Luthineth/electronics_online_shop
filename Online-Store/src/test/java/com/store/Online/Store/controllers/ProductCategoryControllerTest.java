package com.store.Online.Store.controllers;

import com.store.Online.Store.entity.Product;
import com.store.Online.Store.exception.CategoryNotFoundException;
import com.store.Online.Store.exception.ProductNotFoundException;
import com.store.Online.Store.service.productCategoryService;
import com.store.Online.Store.service.productService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

class ProductCategoryControllerTest {

    @Mock
    private productCategoryService productCategoryService;

    @Mock
    private productService productService;

    @InjectMocks
    private ProductCategoryController productCategoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProductsByCategoryAndSubcategories_Successful_ReturnsHttpStatusOk() {
        List<Product> products = new ArrayList<>();
        when(productCategoryService.getProductsByCategoryAndSubcategories(1L)).thenReturn(products);
        ResponseEntity<?> response = productCategoryController.getProductsByCategoryAndSubcategories(1L, new BigDecimal(132), new BigDecimal(200), true, 3, Sort.Direction.ASC);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
    }
    @Test
    void testGetProductsByCategoryAndSubcategories_SuccessfulAllNUll_ReturnsHttpStatusOk() {
        List<Product> products = new ArrayList<>();
        when(productCategoryService.getProductsByCategoryAndSubcategories(1L)).thenReturn(products);
        ResponseEntity<?> response = productCategoryController.getProductsByCategoryAndSubcategories(1L, null, null, null, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
    }

    @Test
    void testGetProductsByCategoryAndSubcategories_CategoryNotFoundException_ReturnsHttpStatusNotFound() {
        Long categoryId = 1L;
        doThrow(new CategoryNotFoundException("Category not found")).when(productCategoryService).getProductsByCategoryAndSubcategories(categoryId);
        ResponseEntity<?> response = productCategoryController.getProductsByCategoryAndSubcategories(categoryId, null, null, null, null, null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Category not found", response.getBody());
    }

    @Test
    void testGetProductsByCategoryAndSubcategories_ProductNotFoundException_ReturnsHttpStatusNotFound() {
        Long categoryId = 1L;
        doThrow(new ProductNotFoundException("Product not found")).when(productCategoryService).getProductsByCategoryAndSubcategories(categoryId);
        ResponseEntity<?> response = productCategoryController.getProductsByCategoryAndSubcategories(categoryId, null, null, null, null, null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product not found", response.getBody());
    }

    @Test
    void testGetProductsByCategoryAndSubcategories_InternalServerError() {
        Long categoryId = 1L;
        doThrow(new RuntimeException("Internal server error")).when(productCategoryService).getProductsByCategoryAndSubcategories(categoryId);
        ResponseEntity<?> response = productCategoryController.getProductsByCategoryAndSubcategories(categoryId, null, null, null, null, null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred while retrieving products by category and subcategories.", response.getBody());
    }
}
