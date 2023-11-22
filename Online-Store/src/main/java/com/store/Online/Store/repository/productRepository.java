package com.store.Online.Store.repository;

import com.store.Online.Store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface productRepository extends JpaRepository<Product,Long> {

    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = :newStockQuantity WHERE p.productId = :productId")
    void updateStockQuantity(@Param("productId") Long productId, @Param("newStockQuantity") int newStockQuantity);

    @Query("SELECT p.imageUrl FROM Product p WHERE p.productId = :productId")
    String findImageUrlByProductId(@Param("productId") Long productId);
}
