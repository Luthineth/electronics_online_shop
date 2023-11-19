package com.store.Online.Store.repositoryTest;

import com.store.Online.Store.entity.Discount;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.repository.productRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigDecimal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;



@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private productRepository productRepository;

    @Test
    void testUpdateStockQuantity() {

        Product existentProduct = new Product(
                "Смартфон Apple iPhone 14",
                "У iPhone 14 Pro ",
                0,
                new BigDecimal("89988.00"),
                new BigDecimal("89988.00"),
                "iphone14_image.jpg",
                new Discount(new BigDecimal(10)));
        existentProduct.setProductId(1L);

        int newStockQuantity = 50;
        productRepository.updateStockQuantity(existentProduct.getProductId(), newStockQuantity);

        Product updatedProduct = productRepository.findById(existentProduct.getProductId()).orElse(null);

        assertThat(updatedProduct, notNullValue());
        assertThat(updatedProduct.getStockQuantity(), equalTo(newStockQuantity));    }

}
