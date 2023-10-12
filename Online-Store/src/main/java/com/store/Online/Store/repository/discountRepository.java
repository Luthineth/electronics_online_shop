package com.store.Online.Store.repository;

import com.store.Online.Store.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface discountRepository extends JpaRepository<Discount, Long> {
}
