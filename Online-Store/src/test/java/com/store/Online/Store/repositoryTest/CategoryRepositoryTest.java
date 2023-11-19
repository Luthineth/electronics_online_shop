package com.store.Online.Store.repositoryTest;

import com.store.Online.Store.entity.Category;
import com.store.Online.Store.repository.categoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;



import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private categoryRepository categoryRepository;

    private Category parentCategory;

    private Category subCategory1;

    private Category subCategory2;

    @BeforeEach
    void setUp() {
        parentCategory = new Category("Аудиотехника", null);
        parentCategory.setCategoryId(2L);

        subCategory1 = new Category("Портативные колонки", parentCategory);
        subCategory1.setCategoryId(8L);

        subCategory2 = new Category("Наушники", parentCategory);
        subCategory2.setCategoryId(9L);

    }
    @Test
    void testFindSubCategories() {
        List<Category> subCategories = categoryRepository.findSubCategories(parentCategory.getCategoryId());
        System.out.println("Returned Subcategories: " + subCategories);
        assertThat(subCategories).hasSize(2);
        assertThat(subCategories).contains(subCategory1, subCategory2);
    }

    @Test
    void testFindCategoryNameByCategoryId() {
        String categoryName = categoryRepository.findCategoryNameByCategoryId(parentCategory.getCategoryId());
        assertThat(categoryName).isEqualTo("АудиоТехника");
    }
    @Test
    void testSaveCategory() {
        Category categoryToSave = new Category("Новая категория", null);

        Category savedCategory = categoryRepository.save(categoryToSave);

        Category retrievedCategory = categoryRepository.findById(savedCategory.getCategoryId()).orElse(null);

        assertThat(retrievedCategory).isNotNull();
        assertThat(retrievedCategory.getCategoryId()).isNotNull();
        assertThat(retrievedCategory.getCategoryName()).isEqualTo("Новая категория");
        assertThat(retrievedCategory.getParentCategories()).isNull();
    }
}
