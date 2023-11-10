package com.store.Online.Store.repository;

import com.store.Online.Store.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface categoryRepository extends JpaRepository<Category,Long> {

    @Query("SELECT c FROM Category c " +
            "WHERE c.parentCategoryId.categoryId = :categoryId")
    List<Category> findSubCategories(@Param("categoryId") Long categoryId);

    @Query("SELECT c.categoryName FROM Category c WHERE c.categoryId = :categoryId")
    String findCategoryNameByCategoryId(@Param("categoryId") Long categoryId);
}
