package com.store.Online.Store.repository;

import com.store.Online.Store.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface orderItemRepository extends JpaRepository<OrderItem,Long> {
}
