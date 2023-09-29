package com.store.Online.Store.repository;

import com.store.Online.Store.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface productCategoryRepository extends JpaRepository<ProductCategory,Long> {
}
