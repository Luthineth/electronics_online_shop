package com.store.Online.Store.repositoryTest;

import com.store.Online.Store.entity.Category;
import com.store.Online.Store.entity.Discount;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.entity.ProductCategory;
import com.store.Online.Store.repository.categoryRepository;
import com.store.Online.Store.repository.productCategoryRepository;
import com.store.Online.Store.repository.productRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DataJpaTest
public class ProductCategoryRepositoryTest {

    @Autowired
    private productCategoryRepository productCategoryRepository;

    @Autowired
    private categoryRepository categoryRepository;

    private  Category category;

    private Product product1;

    @BeforeEach
    void setUp() {
        Category parentcategory = new Category(
                "Смартфоны",
                null);

        category = new Category("Apple", parentcategory);
        category.setCategoryId(4L);

        product1 = new Product(
                "Смартфон Apple iPhone 14",
                "У iPhone 14 Pro ",
                0,
                new BigDecimal("89988.00"),
                new BigDecimal("89988.00"),
                "iphone14_image.jpg",
                new Discount(new BigDecimal(10)));
        product1.setProductId(1L);

        Product product2 = new Product(
                "Смартфон Apple iPhone 13",
                "iPhone 13",
                200,
                new BigDecimal("77999.00"),
                new BigDecimal("77999.00"),
                "iphone13_image.jpg",
                new Discount(new BigDecimal(10)));
        product2.setProductId(2L);
    }

    @Test
    void testFindByCategoryId() {
        List<Product> foundProducts = productCategoryRepository.findByCategoryId(category.getCategoryId());
        assertEquals(2, foundProducts.size());
    }

    @Test
    @Transactional
    void testDeleteByProductId() {
        productCategoryRepository.deleteByProductId(product1);
        List<Product> foundProducts = productCategoryRepository.findByCategoryId(category.getCategoryId());
        assertEquals(1, foundProducts.size());
    }

    @Test
    void testDeleteByCategoryId() {
        productCategoryRepository.deleteByCategoryId(category);
        List<Product> foundProducts = productCategoryRepository.findByCategoryId(category.getCategoryId());
        assertEquals(0, foundProducts.size());
    }

    @Test
    void testSaveProductWithCategory() {

        Category categoryToSave = new Category(
                "Новая категория",
                null);
        categoryRepository.save(categoryToSave);


        ProductCategory productCategory = new ProductCategory(product1,categoryToSave);
        productCategoryRepository.save(productCategory);

        List<Product> foundProducts = productCategoryRepository.findByCategoryId(categoryToSave.getCategoryId());
        assertEquals(1, foundProducts.size());
        assertEquals(product1.getProductName(), foundProducts.get(0).getProductName());
    }
}
