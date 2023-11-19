package com.store.Online.Store.repositoryTest;

import com.store.Online.Store.repository.discountRepository;
import com.store.Online.Store.entity.Discount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DiscountRepositoryTest {

    @Autowired
    private discountRepository discountRepository;

    private final Discount existentDiscount;

    {
        existentDiscount = new Discount(new BigDecimal("10.00"));
        existentDiscount.setDiscountId(2L);
    }
    @Test
    void testFindDiscount() {

        Optional<Discount> foundDiscount = discountRepository.findDiscountsByDiscountPercentage(existentDiscount.getDiscountPercentage());

        assertThat(foundDiscount).isPresent();
        assertThat(foundDiscount.get().getDiscountPercentage()).isEqualByComparingTo(existentDiscount.getDiscountPercentage());
    }
    @Test
    void testFindDiscountByNonexistentPercentage() {
        BigDecimal nonexistentPercentage = new BigDecimal("24.0");
        Optional<Discount> foundDiscount = discountRepository.findDiscountsByDiscountPercentage(nonexistentPercentage);

        assertThat(foundDiscount).isEmpty();
    }
}