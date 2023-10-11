package com.store.Online.Store.service.impl;

import com.store.Online.Store.dto.CommentRequest;
import com.store.Online.Store.entity.Comment;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.entity.User;
import com.store.Online.Store.exception.CommentNotFoundException;
import com.store.Online.Store.exception.ProductNotFoundException;
import com.store.Online.Store.exception.UserNotFoundException;
import com.store.Online.Store.repository.userRepository;
import com.store.Online.Store.service.commentService;
import com.store.Online.Store.repository.commentRepository;
import com.store.Online.Store.service.productService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements commentService{

    private final commentRepository commentRepository;
    private final productService productService;
    private final userRepository userRepository;


    @Autowired
    public CommentServiceImpl(commentRepository commentrepository, productService productService, userRepository userRepository){
        this.commentRepository =commentrepository;
        this.productService = productService;
        this.userRepository = userRepository;
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

        User user;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            throw new UserNotFoundException("User not found with email " + userEmail);
        }
        comment.setUserId(user);

        Optional<Product> productOptional = productService.getProductById(commentRequest.getProductId());
        if (productOptional.isPresent()) {
            comment.setProductId(productOptional.get());
        } else {
            throw new ProductNotFoundException("Product not found with ID: " + productOptional);
        }

        comment.setText(commentRequest.getText());
        comment.setRating(commentRequest.getRating());
        comment.setImageUrl(commentRequest.getImageUrl());
        return commentRepository.save(comment);
    }

    @Override
    public void deleteImage(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException("Comment not found with ID: " + commentId);
        }

        commentRepository.deleteImageByCommentId(commentId);
    }

    @Override
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException("Comment not found with ID: " + commentId);
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<Comment> getCommentsByProductIdSortedByRating(Long productId, Sort.Direction direction) {
        Optional<Product> productOptional = productService.getProductById(productId);
        Product product = productOptional.orElseThrow(() -> new ProductNotFoundException("Error id product"));

        Sort sort = Sort.by(direction, "rating");

        return commentRepository.findByProductId(product, sort);
    }

}
