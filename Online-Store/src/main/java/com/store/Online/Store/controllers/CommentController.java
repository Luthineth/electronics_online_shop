package com.store.Online.Store.controllers;

import com.store.Online.Store.dto.CommentRequest;
import com.store.Online.Store.entity.Comment;
import com.store.Online.Store.service.commentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<Comment>> getCommentsByProductId(@PathVariable Long productId) {
        List<Comment> comments = commentService.getCommentsByProductIdSortedByRating(productId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody CommentRequest comment) {
        Comment addedComment = commentService.addComment(comment);
        return ResponseEntity.ok(addedComment);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{commentId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long commentId) {
        commentService.deleteImage(commentId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
