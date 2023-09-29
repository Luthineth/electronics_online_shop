package com.store.Online.Store.repository;

import com.store.Online.Store.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface discountRepository extends JpaRepository<Discount,Long> {
}
