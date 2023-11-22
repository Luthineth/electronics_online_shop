package com.store.Online.Store.service;

import com.store.Online.Store.dto.CommentRequest;
import com.store.Online.Store.entity.Comment;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface commentService {

    List<CommentRequest> getCommentsByProductId(Long productId);

    List<CommentRequest> getCommentsByProductId(Long productId, Sort.Direction direction);

    Comment addComment(CommentRequest comment, MultipartFile file);

    void deleteComment(Long commentId);

    void deleteImage(Long commentId);

    void deleteProductComments(Long productId);

    Resource getImageContent(String imageName);
}
