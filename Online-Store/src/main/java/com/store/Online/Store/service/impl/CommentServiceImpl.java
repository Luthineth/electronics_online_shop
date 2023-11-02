package com.store.Online.Store.service.impl;

import com.store.Online.Store.dto.CommentRequest;
import com.store.Online.Store.entity.Comment;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.entity.User;
import com.store.Online.Store.exception.*;
import com.store.Online.Store.repository.productRepository;
import com.store.Online.Store.repository.userRepository;
import com.store.Online.Store.service.commentService;
import com.store.Online.Store.repository.commentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements commentService{

    private final commentRepository commentRepository;
    private final productRepository productRepository;
    private final userRepository userRepository;

    @Autowired
    public CommentServiceImpl(commentRepository commentrepository, productRepository productRepository, userRepository userRepository){
        this.commentRepository =commentrepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<CommentRequest> getCommentsByProductId(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        Product product = productOptional.orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        List<Comment> comments = commentRepository.findByProductId(product);
        return mapToCommentRequestList(comments);
    }

    @Override
    public List<CommentRequest> getCommentsByProductId(Long productId, Sort.Direction direction) {
        Optional<Product> productOptional = productRepository.findById(productId);
        Product product = productOptional.orElseThrow(() -> new ProductNotFoundException("Error id product"));

        Sort sort = Sort.by(direction, "rating");

        List<Comment> comments = commentRepository.findByProductId(product, sort);
        return mapToCommentRequestList(comments);
    }

    @Transactional
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

        Optional<Product> productOptional = productRepository.findById(commentRequest.getProductId());
        if (productOptional.isPresent()) {
            comment.setProductId(productOptional.get());
        } else {
            throw new ProductNotFoundException("Product not found with ID: " + productOptional);
        }

        comment.setText(commentRequest.getText());
        comment.setRating(commentRequest.getRating());
        comment.setImageUrl(commentRequest.getImageUrl());

        try {
            return commentRepository.save(comment);
        } catch (Exception e) {
            throw new CommentAdditionException("Failed to add comment" + e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteImage(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException("Comment not found with ID: " + commentId);
        }

        try {
            commentRepository.deleteImageByCommentId(commentId);
        } catch (Exception e) {
            throw new CommentDeletionException("Failed to delete image comment" + e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException("Comment not found with ID: " + commentId);
        }

        try {
            commentRepository.deleteById(commentId);
        } catch (Exception e) {
            throw new CommentDeletionException("Failed to delete comment" + e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteProductComments(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        Product product = productOptional.orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        commentRepository.deleteByProductId(product);
    }
    private List<CommentRequest> mapToCommentRequestList(List<Comment> comments) {
        List<CommentRequest> commentRequests = new ArrayList<>();
        for (Comment comment : comments) {
            commentRequests.add(mapToCommentRequest(comment));
        }
        return commentRequests;
    }

    private CommentRequest mapToCommentRequest(Comment comment) {
        return CommentRequest.builder()
                .firstName(comment.getUserId().getFirstName())
                .text(comment.getText())
                .rating(comment.getRating())
                .imageUrl(comment.getImageUrl())
                .commentId(comment.getCommentId())
                .build();
    }
}
