package com.store.Online.Store.controllers;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.exception.ProductNotFoundException;
import com.store.Online.Store.service.productCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class ProductCategoryController {

    private final productCategoryService productCategoryService;

    @Autowired
    public ProductCategoryController(productCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @GetMapping("/products_category/{categoryId}")
    public ResponseEntity<?> getProductsByCategoryAndSubcategories(@PathVariable Long categoryId) {
        try {
            List<Product> products = productCategoryService.getProductsByCategoryAndSubcategories(categoryId);
            return ResponseEntity.ok(products);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving products by category and subcategories.");
        }
    }
}