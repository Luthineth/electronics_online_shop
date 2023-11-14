package com.store.Online.Store.service.impl;

import com.store.Online.Store.dto.CategoryRequest;
import com.store.Online.Store.exception.*;
import com.store.Online.Store.repository.categoryRepository;
import com.store.Online.Store.repository.productCategoryRepository;
import com.store.Online.Store.service.categoryService;
import com.store.Online.Store.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CategoryServiceImpl implements categoryService {
    private final categoryRepository categoryRepository;
    private final productCategoryRepository productCategoryRepository;

    @Autowired
    public CategoryServiceImpl(categoryRepository categoryRepository, com.store.Online.Store.repository.productCategoryRepository productCategoryRepository){
        this.categoryRepository = categoryRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    public List<Category> getSubCategories() {
        try {
            return categoryRepository.findAll();
        } catch (Exception e) {
            throw new CategoryNotFoundException("Failed to retrieve subcategories. Reason: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public Category addCategory(CategoryRequest categoryRequest) {

        Category category = mapToCategory(categoryRequest);

        try {
            return categoryRepository.save(category);
        } catch (Exception e) {
            throw new CategoryAdditionException("Failed to add category. Reason: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public Category updateCategory(Long categoryId, CategoryRequest categoryRequest) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + categoryId));

        if (categoryRequest.getParentCategoryId() == null) {
            category.setParentCategoryId(null);
        } else {
            Category parentCategory = new Category();
            parentCategory.setCategoryId(categoryRequest.getParentCategoryId());
            category.setParentCategoryId(parentCategory);
        }
        if (categoryRequest.getCategoryName() != null) {
            category.setCategoryName(categoryRequest.getCategoryName());
        } else {
            throw new NoCategoryName("Enter the category name ");
        }

        if (categoryRequest.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(categoryRequest.getParentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Parent category not found with ID: " + categoryRequest.getParentCategoryId()));
            category.setParentCategoryId(parentCategory);
        }

        try {
            return categoryRepository.save(category);
        } catch (Exception e) {
            throw new CategoryUpdateException("Failed to update category. Reason: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + categoryId));
        List<Category> subCategories = categoryRepository.findSubCategories(categoryId);
        if (!subCategories.isEmpty() )
            throw new SubCategoryNotFoundException("This category has subcategories, so you can't delete this category, first delete the subcategories");
        productCategoryRepository.deleteByCategoryId(category);

        try {
            categoryRepository.delete(category);
        } catch (Exception e) {
            throw new CategoryDeletionException("Failed to delete category. Reason: " + e.getMessage());
        }
    }

    public Category mapToCategory(CategoryRequest request) {
        Category category = new Category();

        if (request.getCategoryName() != null) {
            category.setCategoryName(request.getCategoryName());
        } else {
            throw new NoCategoryName("Enter the category name ");
        }

        if (request.getParentCategoryId() == null) {
            category.setParentCategoryId(null);
        } else {
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Parent category not found with ID: " + request.getParentCategoryId()));

            parentCategory.setCategoryName(categoryRepository.findCategoryNameByCategoryId(request.getParentCategoryId()));
            category.setParentCategoryId(parentCategory);
        }
        return category;
    }
}
