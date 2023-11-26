package com.store.Online.Store.service;
import com.store.Online.Store.dto.CommentRequest;
import com.store.Online.Store.entity.Comment;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.entity.User;
import com.store.Online.Store.exception.*;
import com.store.Online.Store.repository.commentRepository;
import com.store.Online.Store.repository.productRepository;
import com.store.Online.Store.repository.userRepository;
import com.store.Online.Store.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private commentRepository commentRepository;

    @Mock
    private productRepository productRepository;

    @Mock
    private userRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getCommentsByProductId_ReturnsListOfCommentRequests() {
        Long productId = 1L;
        Product product = new Product();
        product.setProductId(productId);

        User user = new User();
        user.setFirstName("TestName");
        user.setUserId(1L);

        List<Comment> comments = new ArrayList<>();

        Comment comment1 = new Comment();
        comment1.setUserId(user);
        comments.add(comment1);

        Comment comment2 = new Comment();
        comment2.setUserId(user);
        comments.add(comment2);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(commentRepository.findByProductId(product)).thenReturn(comments);

        List<CommentRequest> result = commentService.getCommentsByProductId(productId);

        assertNotNull(result);
        assertEquals(comments.size(), result.size());
        verify(productRepository, times(1)).findById(productId);
        verify(commentRepository, times(1)).findByProductId(product);
    }

    @Test
    void getCommentsByProductIdAndDirection_ReturnsListOfCommentRequests() {
        Long productId = 1L;
        Product product = new Product();
        product.setProductId(productId);

        List<Comment> comments = new ArrayList<>();

        User user = new User();
        user.setFirstName("TestName");
        user.setUserId(1L);

        Comment comment1 = new Comment();
        comment1.setUserId(user);
        comment1.setRating(3);
        comments.add(comment1);

        Comment comment2 = new Comment();
        comment2.setUserId(user);
        comment2.setRating(5);
        comments.add(comment2);

        Sort.Direction direction = Sort.Direction.ASC;

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(commentRepository.findByProductId(product, Sort.by(direction, "rating"))).thenReturn(comments);

        List<CommentRequest> result = commentService.getCommentsByProductId(productId, direction);

        assertNotNull(result);
        assertEquals(comments.size(), result.size());
        verify(productRepository, times(1)).findById(productId);
        verify(commentRepository, times(1)).findByProductId(product, Sort.by(direction, "rating"));
    }

    @Test
    void addComment_InvalidProductId_ThrowsProductNotFoundException() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setProductId(1L);

        User user = new User();
        user.setEmail("Test@com.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        when(productRepository.findById(commentRequest.getProductId())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> commentService.addComment(commentRequest, null));
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(productRepository, times(1)).findById(commentRequest.getProductId());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addComment_InvalidRating_ThrowsIllegalArgumentException() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setProductId(1L);
        commentRequest.setRating(6);

        User user = new User();
        user.setEmail("Test@com.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        when(productRepository.findById(commentRequest.getProductId())).thenReturn(Optional.of(new Product()));

        assertThrows(IllegalArgumentException.class, () -> commentService.addComment(commentRequest, null));
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(productRepository, times(1)).findById(commentRequest.getProductId());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void deleteImage_ValidCommentId_ImageDeletedSuccessfully() {
        Long commentId = 1L;
        when(commentRepository.existsById(commentId)).thenReturn(true);

        commentService.deleteImage(commentId);

        verify(commentRepository, times(1)).existsById(commentId);
        verify(commentRepository, times(1)).deleteImageByCommentId(commentId);
    }

    @Test
    void deleteImage_InvalidCommentId_ThrowsCommentNotFoundException() {
        Long commentId = 1L;
        when(commentRepository.existsById(commentId)).thenReturn(false);

        assertThrows(CommentNotFoundException.class, () -> commentService.deleteImage(commentId));
        verify(commentRepository, times(1)).existsById(commentId);
        verify(commentRepository, never()).deleteImageByCommentId(commentId);
    }

    @Test
    void deleteComment_ValidCommentId_CommentDeletedSuccessfully() {
        Long commentId = 1L;
        when(commentRepository.existsById(commentId)).thenReturn(true);

        commentService.deleteComment(commentId);

        verify(commentRepository, times(1)).existsById(commentId);
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void deleteComment_InvalidCommentId_ThrowsCommentNotFoundException() {
        Long commentId = 1L;
        when(commentRepository.existsById(commentId)).thenReturn(false);

        assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(commentId));
        verify(commentRepository, times(1)).existsById(commentId);
        verify(commentRepository, never()).deleteById(commentId);
    }

    @Test
    void deleteProductComments_ValidProductId_ProductCommentsDeletedSuccessfully() {
        Long productId = 1L;
        Product product = new Product();
        product.setProductId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        commentService.deleteProductComments(productId);

        verify(productRepository, times(1)).findById(productId);
        verify(commentRepository, times(1)).deleteByProductId(product);
    }

    @Test
    void deleteProductComments_InvalidProductId_ThrowsProductNotFoundException() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> commentService.deleteProductComments(productId));
        verify(productRepository, times(1)).findById(productId);
        verify(commentRepository, never()).deleteByProductId(any(Product.class));
    }
}
