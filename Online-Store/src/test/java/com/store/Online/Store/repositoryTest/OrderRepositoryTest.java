package com.store.Online.Store.repositoryTest;

import com.store.Online.Store.entity.Order;
import com.store.Online.Store.entity.Role;
import com.store.Online.Store.entity.User;
import com.store.Online.Store.repository.orderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private orderRepository orderRepository;

    private User user;
    @BeforeEach
    void setUp() {

        user = new User(
                "Artem",
                "Lihachev",
                "sofaross228@gmail.com",
                "504598",
                new Role(2L, "ADMIN"));
        user.setUserId(1L);
    }

    @Test
    public void testSaveOrder() {

        Date orderDate = new Date(2023-11-11);
        BigDecimal totalPrice = new BigDecimal("100.00");
        Order order = new Order(orderDate, totalPrice, user);

        orderRepository.save(order);

        Order savedOrder = orderRepository.findById(order.getOrderId()).orElse(null);

        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getOrderId()).isNotNull();
        assertThat(savedOrder.getOrderDate()).isEqualTo(orderDate);
        assertThat(savedOrder.getTotalPrice()).isEqualTo(totalPrice);
        assertThat(savedOrder.getUserId()).isEqualTo(user);
    }
}
