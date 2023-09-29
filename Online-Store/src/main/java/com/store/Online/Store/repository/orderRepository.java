package com.store.Online.Store.repository;

import com.store.Online.Store.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface orderRepository extends JpaRepository<Order,Long> {
}
