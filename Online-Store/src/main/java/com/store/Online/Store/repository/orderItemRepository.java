package com.store.Online.Store.repository;

import com.store.Online.Store.entity.OrderItem;
import com.store.Online.Store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface orderItemRepository extends JpaRepository<OrderItem,Long> {
    void deleteByProductId(Product product);
}
