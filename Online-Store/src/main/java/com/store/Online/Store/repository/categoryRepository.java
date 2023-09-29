package com.store.Online.Store.repository;

import com.store.Online.Store.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface categoryRepository extends JpaRepository<Category,Long> {
    @Query("SELECT c FROM Category c WHERE c.parentCategoryId = :category")
    List<Category> findSubcategoriesByCategory(@Param("category") Category category);
}
