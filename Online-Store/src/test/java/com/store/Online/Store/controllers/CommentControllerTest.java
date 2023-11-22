package com.store.Online.Store.controllers;

import com.store.Online.Store.dto.CommentRequest;
import com.store.Online.Store.entity.*;
import com.store.Online.Store.exception.*;
import com.store.Online.Store.service.commentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CommentControllerTest {

    @Mock
    private commentService commentService;

    @InjectMocks
    private CommentController commentController;

    @Mock
    User user = new User(
            "Artem",
            "Lihachev",
            "sofaross228@gmail.com",
            "504598",
            new Role(2L, "ADMIN"));

    @Mock
    Product product = new Product(
            "Новый смартфон",
            "Описание нового смартфона",
            10,
            new BigDecimal("999.99"),
            new BigDecimal("899.99"),
            "new_phone.jpg",
            new Discount(new BigDecimal(5)));

    @Mock
    Comment comment = new Comment(
            user,
            product,
                "Отличный смартфон, работает быстро и камера супер!",
                    5,
                    "image.txt");

    @Mock
    CommentRequest commentRequest = new CommentRequest(
            "Artem",
            product.getProductId(),
            "Отличный смартфон, работает быстро и камера супер!",
            5,
            "image.txt",
            1L);

    @BeforeEach
    void setUp() {
        openMocks(this);
        user.setUserId(1L);
        product.setProductId(1L);
        comment.setCommentId(1L);
    }

    @Test
    void testAddComment_Success_ReturnsHttpStatusOk() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());
        when(commentService.addComment(commentRequest, file)).thenReturn(comment);
        ResponseEntity<?> response = commentController.addComment(file, commentRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comment, response.getBody());
    }

    @Test
    void testAddComment_UserNotFound_ReturnsHttpStatusNOT_FOUND() {
        MultipartFile file = null;
        doThrow(new UserNotFoundException("User not found")).when(commentService).addComment(commentRequest, file);
        ResponseEntity<?> response = commentController.addComment(file, commentRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void testAddComment_ProductNotFound_ReturnsHttpStatusNOT_FOUND() {
        MultipartFile file = null;
        doThrow(new ProductNotFoundException("Product not found")).when(commentService).addComment(commentRequest, file);
        ResponseEntity<?> response = commentController.addComment(file, commentRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product not found", response.getBody());
    }

    @Test
    void testAddComment_ImageNotLoaded_ReturnsHttpStatusBAD_REQUEST() {
        MultipartFile file = null;
        doThrow(new ImageNotLoadedException("Image not loaded")).when(commentService).addComment(commentRequest, file);
        ResponseEntity<?> response = commentController.addComment(file, commentRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Image not loaded", response.getBody());
    }

    @Test
    void testAddComment_InvalidArgument_ReturnsHttpStatusBAD_REQUEST() {
        CommentRequest commentRequest = new CommentRequest();
        MultipartFile file = null;
        doThrow(new IllegalArgumentException("Invalid argument")).when(commentService).addComment(commentRequest, file);
        ResponseEntity<?> response = commentController.addComment(file, commentRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody());
    }

    @Test
    void testAddComment_CommentAdditionException_ReturnsHttpStatusINTERNAL_SERVER_ERROR() {
        MultipartFile file = null;
        doThrow(new CommentAdditionException("Comment addition exception")).when(commentService).addComment(commentRequest, file);
        ResponseEntity<?> response = commentController.addComment(file, commentRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Comment addition exception", response.getBody());
    }

    @Test
    void testDeleteImage_Success_ReturnsHttpStatusOk() {
        ResponseEntity<?> response = commentController.deleteImage(comment.getCommentId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commentService, times(1)).deleteImage(comment.getCommentId());
    }

    @Test
    void testDeleteImage_CommentNotFound_ReturnsHttpStatusNOT_FOUND() {
        doThrow(new CommentNotFoundException("Comment not found")).when(commentService).deleteImage(comment.getCommentId());
        ResponseEntity<?> response = commentController.deleteImage(comment.getCommentId());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Comment not found", response.getBody());
    }

    @Test
    void testDeleteImage_CommentImageDeletionException_ReturnHttpStatusINTERNAL_SERVER_ERROR() {
        doThrow(new CommentImageDeletionException("Comment image deletion exception"))
                .when(commentService).deleteImage(comment.getCommentId());
        ResponseEntity<?> response = commentController.deleteImage(comment.getCommentId());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Comment image deletion exception", response.getBody());
    }

    @Test
    void testDeleteComment_Success_ReturnHttpStatusOK() {
        ResponseEntity<?> response = commentController.deleteComment(comment.getCommentId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commentService, times(1)).deleteComment(comment.getCommentId());
    }

    @Test
    void testDeleteComment_CommentNotFound_ReturnHttpStatusNOT_FOUND() {
        doThrow(new CommentNotFoundException("Comment not found")).when(commentService).deleteComment(comment.getCommentId());
        ResponseEntity<?> response = commentController.deleteComment(comment.getCommentId());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Comment not found", response.getBody());
    }

    @Test
    void testDeleteComment_CommentDeletionException_ReturnHttpStatusINTERNAL_SERVER_ERROR() {
        doThrow(new CommentDeletionException("Comment deletion exception")).when(commentService).deleteComment(comment.getCommentId());
        ResponseEntity<?> response = commentController.deleteComment(comment.getCommentId());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Comment deletion exception", response.getBody());
    }

    @Test
    void serveImage_ExistingImage_ReturnsOkResponseWithImage() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());
        Resource mockResource = new ByteArrayResource(file.getBytes());
        when(commentService.getImageContent(file.getName())).thenReturn(mockResource);
        ResponseEntity<Resource> responseEntity = commentController.serveImage(file.getName());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(MediaType.IMAGE_PNG, responseEntity.getHeaders().getContentType());
        assertArrayEquals(file.getBytes(), ((ByteArrayResource) responseEntity.getBody()).getByteArray());
        verify(commentService, times(1)).getImageContent(file.getName());
    }

    @Test
    void serveImage_NonExistingImage_ReturnsNotFoundResponse() {
        String imageName = "nonExistingImage.png";
        when(commentService.getImageContent(imageName)).thenReturn(null);
        ResponseEntity<Resource> responseEntity = commentController.serveImage(imageName);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(commentService, times(1)).getImageContent(imageName);
    }

    @Test
    void serveImage_ImageNotLoadedException_ReturnsNotFoundResponse() {
        String imageName = "malformedURL.png";
        when(commentService.getImageContent(imageName)).thenThrow(new ImageNotLoadedException("Simulate image loading error"));
        ResponseEntity<Resource> responseEntity = commentController.serveImage(imageName);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(commentService, times(1)).getImageContent(imageName);
    }

}

