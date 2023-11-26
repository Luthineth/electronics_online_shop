package com.store.Online.Store.controllers;

import com.store.Online.Store.dto.ProductRequest;
import com.store.Online.Store.entity.Discount;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.exception.*;
import com.store.Online.Store.service.productService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class ProductControllerTest {

    @Mock
    private productService productService;

    @InjectMocks
    private ProductController productController;

    @Mock
    private Product product;
    @BeforeEach
    void setUp() {
        openMocks(this);
        product = new Product(
                "Смартфон Apple iPhone 14",
                "У iPhone 14 Pro ",
                0,
                new BigDecimal("89988.00"),
                new BigDecimal("89988.00"),
                "iphone14_image.jpg",
                new Discount(new BigDecimal(10)));
        product.setProductId(1L);

    }

    @Test
    void testGetProductById_Success_ReturnsHttpStatusOK() {
        ProductRequest productRequest = new ProductRequest();
        when(productService.getProductRequestById(product.getProductId(), Sort.Direction.DESC)).thenReturn(productRequest);
        ResponseEntity<?> response = productController.getProductById(product.getProductId(), Sort.Direction.DESC);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productRequest, response.getBody());
    }

    @Test
    void testGetProductById_ProductNotFound_ReturnsHttpStatusNOT_FOUND() {
        when(productService.getProductRequestById(product.getProductId(), Sort.Direction.DESC)).thenThrow(new ProductNotFoundException("Product not found"));
        ResponseEntity<?> response = productController.getProductById(product.getProductId(), Sort.Direction.DESC);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product not found", response.getBody());
    }

    @Test
    void testGetProductById_InternalServerError_ReturnsHttpStatusINTERNAL_SERVER_ERROR() {
        when(productService.getProductRequestById(product.getProductId(), Sort.Direction.DESC)).thenThrow(new RuntimeException());
        ResponseEntity<?> response = productController.getProductById(product.getProductId(), Sort.Direction.DESC);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred: null", response.getBody());
    }

    @Test
    void testAddProduct_Success_ReturnsHttpStatusOK() {
        ProductRequest productRequest = new ProductRequest();
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());
        when(productService.addProduct(productRequest, file)).thenReturn(product);

        ResponseEntity<?> response = productController.addProduct(file, productRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
    }

    @Test
    void testAddProduct_ProductAdditionException_ReturnsHttpStatusBAD_REQUEST() {
        ProductRequest productRequest = new ProductRequest();
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());
        when(productService.addProduct(productRequest, file)).thenThrow(new ProductAdditionException("Error adding product"));

        ResponseEntity<?> response = productController.addProduct(file, productRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error adding product", response.getBody());
    }

    @Test
    void testAddProduct_InternalServerError_ReturnsHttpStatusINTERNAL_SERVER_ERROR() {
        ProductRequest productRequest = new ProductRequest();
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());
        when(productService.addProduct(productRequest, file)).thenThrow(new RuntimeException());

        ResponseEntity<?> response = productController.addProduct(file, productRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred: null", response.getBody());
    }

    @Test
    void testUpdateProduct_SuccessReturnsHttpStatusOK() {
        ProductRequest productRequest = new ProductRequest();
        MultipartFile file = mock(MultipartFile.class);
        Product updatedProduct = new Product();
        when(productService.updateProduct(product.getProductId(), productRequest, file)).thenReturn(updatedProduct);
        ResponseEntity<?> response = productController.updateProduct(product.getProductId(), file, productRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProduct, response.getBody());
    }

    @Test
    void testUpdateProduct_ProductUpdateException_ReturnsHttpStatusBAD_REQUEST() {
        ProductRequest productRequest = new ProductRequest();
        MultipartFile file = mock(MultipartFile.class);
        when(productService.updateProduct(product.getProductId(), productRequest, file)).thenThrow(new ProductUpdateException("Error updating product"));

        ResponseEntity<?> response = productController.updateProduct(product.getProductId(), file, productRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error updating product", response.getBody());
    }

    @Test
    void testUpdateProduct_InternalServerError_ReturnsHttpStatusINTERNAL_SERVER_ERROR() {
        ProductRequest productRequest = new ProductRequest();
        MultipartFile file = mock(MultipartFile.class);
        when(productService.updateProduct(product.getProductId(), productRequest, file)).thenThrow(new RuntimeException());

        ResponseEntity<?> response = productController.updateProduct(product.getProductId(), file, productRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred: null", response.getBody());
    }
    @Test
    void testDeleteProduct_Success_ReturnsHttpStatusOK() {
        doNothing().when(productService).deleteProduct(product.getProductId());
        ResponseEntity<?> response = productController.deleteProduct(product.getProductId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteProduct_ProductDeletionException_ReturnsHttpStatusBAD_REQUEST() {
        String errorMessage = "Error deleting product";
        doThrow(new ProductDeletionException(errorMessage)).when(productService).deleteProduct(product.getProductId());
        ResponseEntity<?> response = productController.deleteProduct(product.getProductId());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void testDeleteProduct_InternalServerError_ReturnsHttpStatusITERNAL_SERCER_ERROR() {
        Long productId = 1L;
        doThrow(new RuntimeException()).when(productService).deleteProduct(productId);
        ResponseEntity<?> response = productController.deleteProduct(productId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred: null", response.getBody());
    }
//    @Test
//    void serveImage_ExistingImage_ReturnsOkResponse() throws IOException {
//        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());
//        Resource mockResource = new ByteArrayResource(file.getBytes());
//        when(productService.getImageContent(file.getName())).thenReturn(mockResource);
//
//        ResponseEntity<?> response = productController.serveImage(file.getName());
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
//        assertArrayEquals(file.getBytes(), ((ByteArrayResource) response.getBody()).getByteArray());
//        verify(productService, times(1)).getImageContent(file.getName());
//    }
//
//    @Test
//    void serveImage_NonExistingImage_ReturnsNotFoundResponse() {
//        String imageName = "nonExistingImage.png";
//        when(productService.getImageContent(imageName)).thenReturn(null);
//        ResponseEntity<?> response = productController.serveImage(imageName);
//        assertNotNull(response);
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        verify(productService, times(1)).getImageContent(imageName);
//        verifyNoMoreInteractions(productService);
//    }
//
//    @Test
//    void serveImage_ImageNotLoadedException_ReturnsInternalServerErrorResponse() {
//        String imageName = "malformedURL.png";
//        when(productService.getImageContent(imageName)).thenThrow(new ImageNotLoadedException("Simulate image loading error"));
//        ResponseEntity<?> responseEntity = productController.serveImage(imageName);
//        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
//        assertNull(responseEntity.getBody());
//        verify(productService, times(1)).getImageContent(imageName);
//    }
}

