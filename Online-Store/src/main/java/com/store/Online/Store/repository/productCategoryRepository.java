package com.store.Online.Store.repository;

import com.store.Online.Store.entity.Product;
import com.store.Online.Store.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface productCategoryRepository extends JpaRepository<ProductCategory, Long> {

    @Query("SELECT pc.productId FROM ProductCategory pc WHERE pc.categoryId.categoryId = :categoryId")
    List<Product> findByCategoryId(Long categoryId);
}