package com.store.Online.Store.service;

import com.store.Online.Store.dto.CommentRequest;
import com.store.Online.Store.entity.Comment;

import org.springframework.data.domain.Sort;

import java.util.List;

public interface commentService {

    public List<Comment> getCommentsByProductIdSortedByRating(Long productId);

    public Comment addComment(CommentRequest comment);

    public void deleteComment(Long commentId);

    public void deleteImage(Long commentId);

    List<Comment> getCommentsByProductIdSortedByRating(Long productId, Sort.Direction direction);
}
