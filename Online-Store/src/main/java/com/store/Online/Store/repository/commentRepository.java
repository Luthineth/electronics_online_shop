package com.store.Online.Store.repository;

import com.store.Online.Store.entity.Comment;
import com.store.Online.Store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface commentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProductId(Product productId);
}