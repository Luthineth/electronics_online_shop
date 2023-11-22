package com.store.Online.Store.controllers;

import com.store.Online.Store.dto.CommentRequest;
import com.store.Online.Store.entity.*;
import com.store.Online.Store.exception.*;
import com.store.Online.Store.service.commentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CommentControllerTest {

    @Mock
    private commentService commentService;

    @InjectMocks
    private CommentController commentController;

    User user = new User(
            "Artem",
            "Lihachev",
            "sofaross228@gmail.com",
            "504598",
            new Role(2L, "ADMIN"));

    Product product = new Product(
            "Новый смартфон",
            "Описание нового смартфона",
            10,
            new BigDecimal("999.99"),
            new BigDecimal("899.99"),
            "new_phone.jpg",
            new Discount(new BigDecimal(5)));

    Comment comment = new Comment(
            user,
            product,
                "Отличный смартфон, работает быстро и камера супер!",
                    5,
                    "image.txt");

    CommentRequest commentRequest = new CommentRequest("Artem",product.getProductId(), "Отличный смартфон, работает быстро и камера супер!",5, "image.txt",1L);
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
    void testDeleteComment_CommentDeletionExceptionReturnHttpStatusINTERNAL_SERVER_ERROR() {
        doThrow(new CommentDeletionException("Comment deletion exception")).when(commentService).deleteComment(comment.getCommentId());
        ResponseEntity<?> response = commentController.deleteComment(comment.getCommentId());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Comment deletion exception", response.getBody());
    }

//    @Test
//    void serveImage_ValidImageName_ReturnsImage() throws Exception {
//        String imageName = "test.png";
//
//
//        mockMvc.perform(get("/comments/images/{imageName}", imageName))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.IMAGE_PNG))
//                .andExpect(header().string("Content-Disposition", "inline;filename=\"" + imageName + "\""));
//    }
//
//    @Test
//    void serveImage_NonExistingImage_ReturnsNotFound() throws Exception {
//        String nonExistingImageName = "nonExisting.png";
//
//        // Здесь также может потребоваться подготовка commentService для обработки случая отсутствия изображения
//
//        mockMvc.perform(get("/comments/images/{imageName}", nonExistingImageName))
//                .andExpect(status().isNotFound());
//    }
}

