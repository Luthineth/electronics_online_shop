package com.store.Online.Store.repository;

import com.store.Online.Store.entity.Comment;
import com.store.Online.Store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface commentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByProductId(Product productId);

    @Modifying
    @Query("UPDATE Comment c SET c.imageUrl = null WHERE c.commentId = :commentId")
    void deleteImageByCommentId(@Param("commentId") Long commentId);
}
