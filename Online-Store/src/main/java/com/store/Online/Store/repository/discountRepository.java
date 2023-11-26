package com.store.Online.Store.repository;

import com.store.Online.Store.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface discountRepository extends JpaRepository<Discount, Long> {
    Optional<Discount> findDiscountsByDiscountPercentage(BigDecimal discountPercentage);
}
