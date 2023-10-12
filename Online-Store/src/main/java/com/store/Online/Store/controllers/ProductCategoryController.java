package com.store.Online.Store.controllers;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.exception.ProductNotFoundException;
import com.store.Online.Store.service.productCategoryService;
import com.store.Online.Store.service.productService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class ProductCategoryController {

    private final productCategoryService productCategoryService;

    private final productService productService;
    @Autowired
    public ProductCategoryController(productCategoryService productCategoryService, com.store.Online.Store.service.productService productService) {
        this.productCategoryService = productCategoryService;
        this.productService = productService;
    }

    @GetMapping("/products_category/{categoryId}")
    public ResponseEntity<?> getProductsByCategoryAndSubcategories(
            @PathVariable Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock) {
        try {
            List<Product> products = productCategoryService.getProductsByCategoryAndSubcategories(categoryId);

            if (minPrice != null || maxPrice != null || inStock != null) {
                products = productService.searchProducts(minPrice, maxPrice, inStock, products);
            }
            return ResponseEntity.ok(products);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving products by category and subcategories.");
        }
    }
}