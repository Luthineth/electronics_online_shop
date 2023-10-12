package com.store.Online.Store.service;

import com.store.Online.Store.entity.Category;

import java.util.List;

public interface categoryService {

    public void moveCategory(Long categoryId, Long newParentCategoryId);

    public void deleteCategory(Long categoryId);

    public Category updateCategory(Category category);

    List<Category> getSubCategories();

    public Category addCategory(Category category);
}
