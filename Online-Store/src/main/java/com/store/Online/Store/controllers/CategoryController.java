package com.store.Online.Store.controllers;

import com.store.Online.Store.dto.CategoryRequest;
import com.store.Online.Store.entity.Category;
import com.store.Online.Store.exception.*;
import com.store.Online.Store.service.categoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class CategoryController {

    private final categoryService categoryService;

    @Autowired
    public CategoryController(categoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/main")
    public ResponseEntity<?> getAllSubCategories() {
        try {
            List<Category> subCategories = categoryService.getSubCategories();
            return ResponseEntity.ok(subCategories);
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving subcategories.");
        }
    }

    @RequestMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody CategoryRequest categoryRequest) {
        try {
            Category addedCategory = categoryService.addCategory(categoryRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedCategory);
        } catch (NoCategoryName e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CategoryAdditionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryRequest categoryRequest) {
        try {
            Category updatedCategory = categoryService.updateCategory(categoryId, categoryRequest);
            return ResponseEntity.ok(updatedCategory);
        } catch (NoCategoryName e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }  catch (CategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CategoryUpdateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok().build();
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SubCategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (CategoryDeletionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}