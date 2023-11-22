package com.store.Online.Store.repositoryTest;

import com.store.Online.Store.entity.*;
import com.store.Online.Store.repository.orderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DataJpaTest
public class OrderItemRepositoryTest {

    @Autowired
    private orderItemRepository orderItemRepository;

    private Product product1;
    private Product product2;
    private Order order;

    @BeforeEach
    void setUp() {

        User user = new User(
                "Artem",
                "Lihachev",
                "sofaross228@gmail.com",
                "504598",
                new Role(2L, "ADMIN"));
        user.setUserId(1L);


        product1 = new Product(
                "Смартфон Apple iPhone 14",
                "У iPhone 14 Pro ",
                0,
                new BigDecimal("89988.00"),
                new BigDecimal("89988.00"),
                "iphone14_image.jpg",
                new Discount(new BigDecimal(10)));
        product1.setProductId(1L);

        product2 = new Product(
                "Смартфон Apple iPhone 13",
                "iPhone 13",
                200,
                new BigDecimal("77999.00"),
                new BigDecimal("77999.00"),
                "iphone13_image.jpg",
                new Discount(new BigDecimal(10)));
        product2.setProductId(2L);

        order = new Order(
                new Date(2023-10-10),
                new BigDecimal("150000.00"),
                user);
        order.setOrderId(1L);

    }

    @Test
    public void testDeleteByProductId() {

        orderItemRepository.deleteByProductId(product1);

        List<OrderItem> orderItemsAfterDeletion = orderItemRepository.findAll();
        assertThat(orderItemsAfterDeletion).hasSize(1);
    }

    @Test
    public void testInsertOrderItems() {
        OrderItem orderItem1 = new OrderItem(order, product1, 2);
        OrderItem orderItem2 = new OrderItem(order, product2, 1);
        orderItemRepository.saveAll(Arrays.asList(orderItem1, orderItem2));

        List<OrderItem> orderItemsAfterInsertion = orderItemRepository.findAll();
        assertThat(orderItemsAfterInsertion).hasSize(4);
    }
}
