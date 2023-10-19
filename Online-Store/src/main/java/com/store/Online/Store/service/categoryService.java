package com.store.Online.Store.service;

import com.store.Online.Store.dto.CategoryRequest;
import com.store.Online.Store.entity.Category;

import java.net.CacheRequest;
import java.util.List;

public interface categoryService {

    void deleteCategory(Long categoryId);

    Category updateCategory(Long categoryId,CategoryRequest categoryRequest);

    List<Category> getSubCategories();

    Category addCategory(CategoryRequest categoryRequest);
}
