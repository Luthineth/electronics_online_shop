package com.store.Online.Store.controllers;

import com.store.Online.Store.dto.ProductRequest;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.exception.*;
import com.store.Online.Store.service.productService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class ProductControllerTest {

    @Mock
    private productService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testGetProductById_Success() {
        Long productId = 1L;
        ProductRequest productRequest = new ProductRequest();
        when(productService.getProductRequestById(productId, Sort.Direction.DESC)).thenReturn(productRequest);

        ResponseEntity<?> response = productController.getProductById(productId, Sort.Direction.DESC);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productRequest, response.getBody());
    }

    @Test
    void testGetProductById_ProductNotFound() {
        Long productId = 1L;
        when(productService.getProductRequestById(productId, Sort.Direction.DESC)).thenThrow(new ProductNotFoundException("Product not found"));

        ResponseEntity<?> response = productController.getProductById(productId, Sort.Direction.DESC);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product not found", response.getBody());
    }

    @Test
    void testGetProductById_InternalServerError() {
        Long productId = 1L;
        when(productService.getProductRequestById(productId, Sort.Direction.DESC)).thenThrow(new RuntimeException());

        ResponseEntity<?> response = productController.getProductById(productId, Sort.Direction.DESC);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody());
    }

    @Test
    void testAddProduct_Success() {
        ProductRequest productRequest = new ProductRequest();
        MultipartFile file = mock(MultipartFile.class);
        Product addedProduct = new Product();
        when(productService.addProduct(productRequest, file)).thenReturn(addedProduct);

        ResponseEntity<?> response = productController.addProduct(file, productRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(addedProduct, response.getBody());
    }

    @Test
    void testAddProduct_ProductAdditionException() {
        ProductRequest productRequest = new ProductRequest();
        MultipartFile file = mock(MultipartFile.class);
        when(productService.addProduct(productRequest, file)).thenThrow(new ProductAdditionException("Error adding product"));

        ResponseEntity<?> response = productController.addProduct(file, productRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error adding product", response.getBody());
    }

    @Test
    void testAddProduct_InternalServerError() {
        ProductRequest productRequest = new ProductRequest();
        MultipartFile file = mock(MultipartFile.class);
        when(productService.addProduct(productRequest, file)).thenThrow(new RuntimeException());

        ResponseEntity<?> response = productController.addProduct(file, productRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody());
    }

    @Test
    void testUpdateProduct_Success() {
        Long productId = 1L;
        ProductRequest productRequest = new ProductRequest();
        MultipartFile file = mock(MultipartFile.class);
        Product updatedProduct = new Product();
        when(productService.updateProduct(productId, productRequest, file)).thenReturn(updatedProduct);

        ResponseEntity<?> response = productController.updateProduct(productId, file, productRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProduct, response.getBody());
    }

    @Test
    void testUpdateProduct_ProductUpdateException() {
        Long productId = 1L;
        ProductRequest productRequest = new ProductRequest();
        MultipartFile file = mock(MultipartFile.class);
        when(productService.updateProduct(productId, productRequest, file)).thenThrow(new ProductUpdateException("Error updating product"));

        ResponseEntity<?> response = productController.updateProduct(productId, file, productRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error updating product", response.getBody());
    }

    @Test
    void testUpdateProduct_InternalServerError() {
        Long productId = 1L;
        ProductRequest productRequest = new ProductRequest();
        MultipartFile file = mock(MultipartFile.class);
        when(productService.updateProduct(productId, productRequest, file)).thenThrow(new RuntimeException());

        ResponseEntity<?> response = productController.updateProduct(productId, file, productRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody());
    }
    @Test
    void testDeleteProduct_Success() {
        Long productId = 1L;
        doNothing().when(productService).deleteProduct(productId);

        ResponseEntity<?> response = productController.deleteProduct(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteProduct_ProductDeletionException() {
        Long productId = 1L;
        String errorMessage = "Error deleting product";
        doThrow(new ProductDeletionException(errorMessage)).when(productService).deleteProduct(productId);

        ResponseEntity<?> response = productController.deleteProduct(productId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void testDeleteProduct_InternalServerError() {
        Long productId = 1L;
        doThrow(new RuntimeException()).when(productService).deleteProduct(productId);

        ResponseEntity<?> response = productController.deleteProduct(productId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody());
    }
    @Test
    void serveImage_ExistingImage_ReturnsOkResponse() {
        // Arrange
        String imageName = "existingImage.png";
        Resource mockResource = mock(Resource.class);
        when(productService.getImageContent(imageName)).thenReturn(mockResource);
        when(mockResource.exists()).thenReturn(true);
        when(mockResource.isReadable()).thenReturn(true);

        // Act
        ResponseEntity<?> response = productController.serveImage(imageName);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
        verify(productService, times(1)).getImageContent(imageName);
        verify(mockResource, times(1)).exists();
        verify(mockResource, times(1)).isReadable();
        verifyNoMoreInteractions(productService, mockResource);
    }

    @Test
    void serveImage_NonExistingImage_ReturnsNotFoundResponse() {
        // Arrange
        String imageName = "nonExistingImage.png";
        when(productService.getImageContent(imageName)).thenReturn(null);

        // Act
        ResponseEntity<?> response = productController.serveImage(imageName);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productService, times(1)).getImageContent(imageName);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void serveImage_ImageNotLoadedException_ReturnsInternalServerErrorResponse() {
        // Arrange
        String imageName = "malformedURL.png";
        when(productService.getImageContent(imageName)).thenThrow(new ImageNotLoadedException("Simulate exception"));

        // Act
        ResponseEntity<?> response = productController.serveImage(imageName);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(productService, times(1)).getImageContent(imageName);
        verifyNoMoreInteractions(productService);
    }
}



//    @Test
//    public void testAddProduct() throws Exception {
//        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
//
//        // Мокаем данные
//        MockMultipartFile multipartFile = new MockMultipartFile(
//                "file",
//                "test.jpg",
//                "image/jpeg",
//                Files.readAllBytes(Paths.get("D:\\javaCode\\electronics_online_shop\\Online-Store\\src\\test\\java\\image\\test.jpg"))
//        );
//
//        // Выполняем запрос и проверяем ответ
//        mockMvc.perform(MockMvcRequestBuilders.multipart("/products")
//                        .file(multipartFile)
//                        .param("productName", "Название продукта")
//                        .param("description", "Описание продукта")
//                        .param("stockQuantity", "10")
//                        .param("price", "50.00")
//                        .contentType(MediaType.MULTIPART_FORM_DATA))
//                .andExpect(status().isCreated());
//        verify(productService, times(1)).addProduct(any(ProductRequest.class), any(MultipartFile.class));
//
//    }
