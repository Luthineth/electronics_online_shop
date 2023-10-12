package com.store.Online.Store.service;

import com.store.Online.Store.entity.Category;

import java.util.List;

public interface categoryService {

    void moveCategory(Long categoryId, Long newParentCategoryId);

    void deleteCategory(Long categoryId);

    Category updateCategory(Category category);

    List<Category> getSubCategories();

    Category addCategory(Category category);
}
