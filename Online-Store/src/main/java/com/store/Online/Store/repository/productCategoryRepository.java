package com.store.Online.Store.repository;

import com.store.Online.Store.entity.Product;
import com.store.Online.Store.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface productCategoryRepository extends JpaRepository<ProductCategory,Long> {

    @Query("SELECT p FROM Product p JOIN p.productCategoryCollection pc WHERE pc.categoryId = :categoryId")
    List<Product> findProductsByCategoryId(@Param("categoryId") Long categoryId);
}
