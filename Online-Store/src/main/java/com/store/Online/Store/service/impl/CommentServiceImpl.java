package com.store.Online.Store.service.impl;

import com.store.Online.Store.dto.CommentRequest;
import com.store.Online.Store.entity.Comment;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.entity.User;
import com.store.Online.Store.exception.ProductNotFoundException;
import com.store.Online.Store.service.commentService;
import com.store.Online.Store.repository.commentRepository;
import com.store.Online.Store.service.productService;
import com.store.Online.Store.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements commentService{

    private final commentRepository commentRepository;
    private final productService productService;
    private final userService userService;


    @Autowired
    public CommentServiceImpl(commentRepository commentrepository, productService productService, com.store.Online.Store.service.userService userService){
        this.commentRepository =commentrepository;
        this.productService = productService;
        this.userService = userService;
    }

    @Override
    public List<Comment> getCommentsByProductIdSortedByRating(Long productId) {
        Optional<Product> productOptional = productService.getProductById(productId);
        Product product = productOptional.orElse(null);

        if (product == null) {
           throw new ProductNotFoundException("Error id product");
        }
        return commentRepository.findByProductId(product);
    }

    @Override
    public Comment addComment(CommentRequest commentRequest) {
        Comment comment = new Comment();
        Optional<User> userOptional = userService.getUserId(commentRequest.getUserId());

        if (userOptional.isPresent()) {
            comment.setUserId(userOptional.get());
        } else {
             throw new IllegalArgumentException("user not found");
        }

        Optional<Product> productOptional = productService.getProductById(commentRequest.getProductId());
        if (productOptional.isPresent()) {
            comment.setProductId(productOptional.get());
        } else {
            throw new IllegalArgumentException("product not found");
        }

        comment.setText(commentRequest.getText());
        comment.setRating(commentRequest.getRating());
        comment.setImageUrl(commentRequest.getImageUrl());
        return commentRepository.save(comment);
    }

    @Override
    public void deleteImage(Long commentId) {
        commentRepository.deleteImageByCommentId(commentId);
    }

    @Override
    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
   public List<Comment> getCommentsByProductIdSortedByRating(Long productId, Sort.Direction direction) {
//        return commentRepository.findByProductIdOrderByRating(productId, direction);
        return new ArrayList<>();
    }

}
