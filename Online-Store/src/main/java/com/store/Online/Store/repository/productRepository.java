package com.store.Online.Store.repository;

import com.store.Online.Store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface productRepository extends JpaRepository<Product,Long> {

    Optional<Product> findByProductId(Long productId);

    Optional<Product> findByProductName(String productName);

    @NonNull
    List<Product> findAll();

    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = :newStockQuantity WHERE p.productId = :productId")
    void updateStockQuantity(@Param("productId") Long productId, @Param("newStockQuantity") int newStockQuantity);
}