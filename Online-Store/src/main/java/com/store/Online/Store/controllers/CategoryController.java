package com.store.Online.Store.controllers;

import com.store.Online.Store.entity.Category;
import com.store.Online.Store.exception.CategoryNotFoundException;
import com.store.Online.Store.service.categoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final categoryService categoryService;

    @Autowired
    public CategoryController(categoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getSubCategories(@PathVariable Long categoryId) {
        try {
            List<Category> subCategories = categoryService.getSubCategories(categoryId);
            return ResponseEntity.ok(subCategories);
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving subcategories.");
        }
    }
}