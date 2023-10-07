package com.store.Online.Store.service;

import com.store.Online.Store.entity.Category;

public interface categoryService {

    public Category getCategoryTree(Long parentId);

    public void moveCategory(Long categoryId, Long newParentCategoryId);

    public void deleteCategory(Long categoryId);

    public Category updateCategory(Category category);

    public Category addCategory(Category category);
}
