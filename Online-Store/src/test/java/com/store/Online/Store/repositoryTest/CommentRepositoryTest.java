package com.store.Online.Store.repositoryTest;

import com.store.Online.Store.entity.*;
import com.store.Online.Store.repository.commentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CommentRepositoryTest {

    @Autowired
    private commentRepository commentRepository;

    private Product product;
    private User user1;

    private Comment comment1;

    private Comment comment2;

    @BeforeEach
    void setUp() {

        user1 = new User(
                "Artem",
                "Lihachev",
                "sofaross228@gmail.com",
                "504598",
                new Role(2L, "ADMIN"));
        user1.setUserId(1L);

        User user2 = new User(
                "Katerina",
                "Katerina",
                "Qwerty@gmail.com",
                "504598",
                new Role(2L, "USER"));
        user2.setUserId(2L);

        product = new Product(
                "Смартфон Apple iPhone 14",
                "У iPhone 14 Pro ",
                0,
                new BigDecimal("89988.00"),
                new BigDecimal("89988.00"),
                "iphone14_image.jpg",
                new Discount(new BigDecimal(10)));
        product.setProductId(1L);

        comment1 = new Comment(
                user1,
                product,
                "Отличный смартфон, работает быстро и камера супер!",
                5,
                "image.txt");
        comment1.setCommentId(1L);

        comment2 = new Comment(
                user2,
                product,
                "заблокирована еСим",
                1,
                null);
        comment2.setCommentId(2L);

    }
    @Test
    void testFindByProductId() {
        commentRepository.saveAll(Arrays.asList(comment1, comment2));
        List<Comment> comments = commentRepository.findByProductId(product);
        assertThat(comments).hasSize(2);
    }

    @Test
    void testDeleteByProductId() {
        commentRepository.deleteByProductId(product);
        List<Comment> comments = commentRepository.findByProductId(product);
        assertThat(comments).isEmpty();
    }

    @Test
    @Transactional
    void testDeleteImageByCommentId() {
        commentRepository.deleteImageByCommentId(comment1.getCommentId());
        Comment updatedComment = commentRepository.findById(comment1.getCommentId()).orElse(null);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getImageUrl()).isNull();
    }

    @Test
    void testSaveComment() {
        Comment comment = new Comment(user1,product,"EEeeeEE",4,"imaageSs.png");
        Comment savedComment = commentRepository.save(comment);

        Comment retrievedComment = commentRepository.findById(savedComment.getCommentId()).orElse(null);

        assertThat(retrievedComment).isNotNull();
        assertThat(retrievedComment.getCommentId()).isNotNull();
        assertThat(retrievedComment.getUserId()).isEqualTo(comment.getUserId());
        assertThat(retrievedComment.getProductId()).isEqualTo(comment.getProductId());
        assertThat(retrievedComment.getText()).isEqualTo(comment.getText());
        assertThat(retrievedComment.getRating()).isEqualTo(comment.getRating());
        assertThat(retrievedComment.getImageUrl()).isEqualTo(comment.getImageUrl());
    }
}
