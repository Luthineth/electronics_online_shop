package com.store.Online.Store.service.impl;

import com.store.Online.Store.exception.CategoryNotFoundException;
import com.store.Online.Store.exception.InvalidCategoryMoveException;
import com.store.Online.Store.exception.SubCategoryNotFoundException;
import com.store.Online.Store.repository.categoryRepository;
import com.store.Online.Store.service.categoryService;
import com.store.Online.Store.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements categoryService {
    private final categoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(categoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }


    @Override
    public List<Category> getSubCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + categoryId));

        categoryRepository.delete(category);
    }


    @Override
    public void moveCategory(Long categoryId, Long newParentCategoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + categoryId));

        Category newParentCategory = categoryRepository.findById(newParentCategoryId)
                .orElseThrow(() -> new SubCategoryNotFoundException("No subcategories found for category with ID: " + newParentCategoryId));

        if (category.getParentCategoryId() != null && category.getParentCategoryId().equals(newParentCategoryId)) {
            throw new InvalidCategoryMoveException("Категория уже имеет указанную родительскую категорию.");
        }

        category.setParentCategoryId(newParentCategory);
        categoryRepository.save(category);
    }

}
