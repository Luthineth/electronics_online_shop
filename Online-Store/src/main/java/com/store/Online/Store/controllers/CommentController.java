package com.store.Online.Store.controllers;

import com.store.Online.Store.dto.CommentRequest;
import com.store.Online.Store.entity.Comment;
import com.store.Online.Store.exception.*;
import com.store.Online.Store.service.commentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final commentService commentService;

    @Autowired
    public CommentController(commentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getCommentsByProductId(@PathVariable Long productId) {
        try {
            List<Comment> comments = commentService.getCommentsByProductIdSortedByRating(productId);
            return ResponseEntity.ok(comments);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving comments.");
        }
    }

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentRequest comment) {
        try {
            Comment addedComment = commentService.addComment(comment);
            return ResponseEntity.ok(addedComment);
        } catch (UserCreationException | ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding a comment.");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @PutMapping("/{commentId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long commentId) {
        try {
            commentService.deleteImage(commentId);
            return ResponseEntity.ok().build();
        } catch (CommentImageDeletionException | CommentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the comment image.");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok().build();
        } catch (CommentDeletionException | CommentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the comment.");
        }
    }

    @GetMapping("/{productId}/sorted")
    public ResponseEntity<?> getCommentsByProductId(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        try {
            List<Comment> comments = commentService.getCommentsByProductIdSortedByRating(productId, direction);
            return ResponseEntity.ok(comments);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving comments.");
        }
    }
}