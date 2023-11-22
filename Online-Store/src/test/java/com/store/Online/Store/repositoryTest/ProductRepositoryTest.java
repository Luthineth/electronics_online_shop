package com.store.Online.Store.repositoryTest;

import com.store.Online.Store.entity.Discount;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.repository.productRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigDecimal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;




@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private productRepository productRepository;

    private Product existentProduct;

    @BeforeEach
    void setUp(){
        existentProduct = new Product(
                "Смартфон Apple iPhone 14",
                "У iPhone 14 Pro ",
                0,
                new BigDecimal("89988.00"),
                new BigDecimal("89988.00"),
                "iphone14_image.jpg",
                new Discount(new BigDecimal(10)));
        existentProduct.setProductId(1L);
    }
    @Test
    void testUpdateStockQuantity() {
        int newStockQuantity = 50;
        productRepository.updateStockQuantity(existentProduct.getProductId(), newStockQuantity);

        Product updatedProduct = productRepository.findById(existentProduct.getProductId()).orElse(null);

        assertThat(updatedProduct, notNullValue());
        assertThat(updatedProduct.getStockQuantity(), equalTo(newStockQuantity));
    }

    @Test
    void testSaveProduct() {
        Product newProduct = new Product(
                "Новый смартфон",
                "Описание нового смартфона",
                10,
                new BigDecimal("999.99"),
                new BigDecimal("899.99"),
                "new_phone.jpg",
                new Discount(new BigDecimal(5)));

        productRepository.save(newProduct);

        Product savedProduct = productRepository.findById(newProduct.getProductId()).orElse(null);

        assertThat(savedProduct).isNotNull();

        assertThat(savedProduct.getProductName()).isEqualTo(newProduct.getProductName());
        assertThat(savedProduct.getDescription()).isEqualTo(newProduct.getDescription());
        assertThat(savedProduct.getStockQuantity()).isEqualTo(newProduct.getStockQuantity());
        assertThat(savedProduct.getPrice()).isEqualTo(newProduct.getPrice());
        assertThat(savedProduct.getPriceWithDiscount()).isEqualTo(newProduct.getPriceWithDiscount());
        assertThat(savedProduct.getImageUrl()).isEqualTo(newProduct.getImageUrl());
        assertThat(savedProduct.getDiscountId().getDiscountPercentage())
                .isEqualTo(newProduct.getDiscountId().getDiscountPercentage());
    }

    @Test
    void testFindImageUrlByProductId() {
        String imageUrl = productRepository.findImageUrlByProductId(existentProduct.getProductId());

        assertThat(imageUrl).isNotNull();
        assertThat(imageUrl).isEqualTo(existentProduct.getImageUrl());
    }
}
