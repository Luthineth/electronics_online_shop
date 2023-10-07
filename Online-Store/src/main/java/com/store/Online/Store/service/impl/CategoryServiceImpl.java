package com.store.Online.Store.service.impl;

import com.store.Online.Store.repository.categoryRepository;
import com.store.Online.Store.service.categoryService;
import com.store.Online.Store.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements categoryService {
    private final categoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(categoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category getCategoryTree(Long parentId) {
        return categoryRepository.findCategoryTree(parentId);
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
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public void moveCategory(Long categoryId, Long newParentCategoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        Category newParentCategory = categoryRepository.findById(newParentCategoryId).orElse(null);

        if (category != null && newParentCategory != null) {
            category.setParentCategoryId(newParentCategory);
            categoryRepository.save(category);
        }
    }

}
