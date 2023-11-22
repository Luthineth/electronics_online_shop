package com.store.Online.Store.service;

import com.store.Online.Store.dto.CommentRequest;
import com.store.Online.Store.dto.ProductRequest;
import com.store.Online.Store.entity.*;
import com.store.Online.Store.exception.*;
import com.store.Online.Store.repository.*;
import com.store.Online.Store.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    @Mock
    private productRepository productRepository;

    @Mock
    private productCategoryRepository productCategoryRepository;

    @Mock
    private categoryRepository categoryRepository;

    @Mock
    private discountRepository discountRepository;

    @Mock
    private commentService commentService;

    @Mock
    private orderItemRepository orderItemRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(productService, "directoryPath", "D:\\javaCode\\electronics_online_shop\\Online-Store\\images");
    }

    @Test
    void getProductById_ExistingProductId_ReturnsProduct() {
        Long productId = 1L;
        Product product = new Product();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Optional<Product> result = productService.getProductById(productId);
        assertTrue(result.isPresent());
        assertEquals(product, result.get());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void getProductById_NonExistingProductId_ThrowsProductNotFoundException() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void getProductRequestById_ExistingProductId_ReturnsProductRequest() {
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("Test");
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(commentService.getCommentsByProductId(eq(product.getProductId()), any(Sort.Direction.class)))
                .thenReturn(Arrays.asList(new CommentRequest()));

        ProductRequest result = productService.getProductRequestById(product.getProductId(), Sort.Direction.ASC);

        assertNotNull(result);
        assertEquals(product.getProductName(), result.getProductName());
        verify(productRepository, times(1)).findById(product.getProductId());
        verify(commentService, times(1)).getCommentsByProductId(product.getProductId(), Sort.Direction.ASC);
    }

    @Test
    void getProductRequestById_NonExistingProductId_ThrowsProductNotFoundException() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> productService.getProductRequestById(productId, Sort.Direction.ASC));
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void addProduct_ValidProductRequestAndFile_ProductAddedSuccessfully() {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName("Test Product");
        productRequest.setDescription("Test Description");
        productRequest.setStockQuantity(10);
        productRequest.setPrice(BigDecimal.valueOf(100.00));
        productRequest.setCategoryId(Arrays.asList(1L, 2L));

        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());

        when(discountRepository.findDiscountsByDiscountPercentage(any(BigDecimal.class)))
                .thenReturn(Optional.of(new Discount()));
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(new Category()));
        when(productRepository.save(any(Product.class))).thenReturn(new Product());

        Product result = productService.addProduct(productRequest, file);

        assertNotNull(result);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void addProduct_NullFile_DefaultImageUsed() {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName("Test Product");
        productRequest.setDescription("Test Description");
        productRequest.setStockQuantity(10);
        productRequest.setPrice(BigDecimal.valueOf(100.00));
        productRequest.setCategoryId(Arrays.asList(1L, 2L));

        when(discountRepository.findDiscountsByDiscountPercentage(any(BigDecimal.class)))
                .thenReturn(Optional.of(new Discount()));
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(new Category()));
        when(productRepository.save(any(Product.class))).thenReturn(new Product());

        Product result = productService.addProduct(productRequest, null);

        assertNotNull(result);
        assertEquals("D:\\javaCode\\electronics_online_shop\\Online-Store\\images/2.png", result.getImageUrl());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_ExistingProductId_ProductUpdatedSuccessfully() {
        Long productId = 1L;
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName("Updated Product");
        productRequest.setDescription("Updated Description");
        productRequest.setStockQuantity(20);
        productRequest.setPrice(BigDecimal.valueOf(150.00));
        productRequest.setDiscountPercentage(new BigDecimal("10.00"));
        productRequest.setCategoryId(Collections.singletonList(3L));

        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());

        Product existingProduct = new Product(
                "Новый смартфон",
                "Описание нового смартфона",
                10,
                new BigDecimal("999.99"),
                new BigDecimal("899.99"),
                "new_phone.jpg",
                new Discount(new BigDecimal(5)));
        existingProduct.setProductId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(discountRepository.findDiscountsByDiscountPercentage(any(BigDecimal.class)))
                .thenReturn(Optional.of(new Discount(new BigDecimal("10.00"))));
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(new Category()));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        Product result = productService.updateProduct(productId, productRequest, file);

        assertNotNull(result);
        assertEquals("Updated Product", result.getProductName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(20, result.getStockQuantity());
        assertEquals(BigDecimal.valueOf(150.00), result.getPrice());
        assertTrue(result.getImageUrl().matches(".*test.jpg"));
        verify(productRepository, times(1)).findById(productId);
        verify(discountRepository, times(1))
                .findDiscountsByDiscountPercentage(any(BigDecimal.class));
        verify(categoryRepository, times(1)).findById(any(Long.class));
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_NonExistingProductId_ThrowsProductNotFoundException() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(productId, new ProductRequest(), null));
        verify(productRepository, times(1)).findById(productId);
        verifyNoMoreInteractions(discountRepository, categoryRepository, productRepository);
    }

    @Test
    void updateProduct_NonExistingDiscountId_ThrowsDiscountNotFoundException() {
        Product existingProduct = new Product();
        existingProduct.setProductId(1L);

        when(productRepository.findById(existingProduct.getProductId())).thenReturn(Optional.of(existingProduct));
        when(discountRepository.findDiscountsByDiscountPercentage(any(BigDecimal.class)))
                .thenReturn(Optional.empty());
        assertThrows(DiscountNotFoundException.class, () -> productService.updateProduct(existingProduct.getProductId(), new ProductRequest(), null));
        verify(productRepository, times(1)).findById(existingProduct.getProductId());
    }

    @Test
    void updateProduct_NonExistingCategoryId_ThrowsCategoryNotFoundException() {
        Long productId = 1L;
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName("Updated Product");
        productRequest.setDescription("Updated Description");
        productRequest.setStockQuantity(20);
        productRequest.setPrice(BigDecimal.valueOf(150.00));
        productRequest.setDiscountPercentage(new BigDecimal("10.00"));
        productRequest.setCategoryId(Collections.singletonList(3L));

        Product existingProduct = new Product(
                "Новый смартфон",
                "Описание нового смартфона",
                10,
                new BigDecimal("999.99"),
                new BigDecimal("899.99"),
                "new_phone.jpg",
                new Discount(new BigDecimal(5)));
        existingProduct.setProductId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(discountRepository.findDiscountsByDiscountPercentage(any(BigDecimal.class)))
                .thenReturn(Optional.of(new Discount(new BigDecimal("10.00"))));
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> productService.updateProduct(existingProduct.getProductId(), productRequest, null));
        verify(productRepository, times(1)).findById(existingProduct.getProductId());
        verify(discountRepository, times(1))
                .findDiscountsByDiscountPercentage(any(BigDecimal.class));
        verify(categoryRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void deleteProduct_ExistingProductId_ProductDeletedSuccessfully() {
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setProductId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        productService.deleteProduct(productId);

        verify(productRepository, times(1)).findById(productId);
        verify(orderItemRepository, times(1)).deleteByProductId(existingProduct);
        verify(commentService, times(1)).deleteProductComments(productId);
        verify(productCategoryRepository, times(1)).deleteByProductId(existingProduct);
        verify(productRepository, times(1)).deleteById(existingProduct.getProductId());
    }

    @Test
    void deleteProduct_NonExistingProductId_ThrowsProductNotFoundException() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(productId));
        verify(productRepository, times(1)).findById(productId);
        verifyNoMoreInteractions(orderItemRepository, commentService, productCategoryRepository);
    }

    @Test
    void searchProducts_FilteredProducts_ReturnsFilteredProducts() {
        // Arrange
        BigDecimal minPrice = BigDecimal.valueOf(110.00);
        BigDecimal maxPrice = BigDecimal.valueOf(250.00);
        Boolean inStock = true;
        Integer minRating = null;
        Sort.Direction price = Sort.Direction.ASC;

        List<Product> products = Arrays.asList(
                createProduct(1L, "Product1", BigDecimal.valueOf(100.00)),
                createProduct(2L, "Product2", BigDecimal.valueOf(120.00)),
                createProduct(3L, "Product3", BigDecimal.valueOf(800.00)),
                createProduct(4L, "Product4", BigDecimal.valueOf(200.00))
        );

        List<Product> result = productService.searchProducts(minPrice, maxPrice, inStock, minRating, price, products);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Product2", result.get(0).getProductName());
        assertEquals("Product4", result.get(1).getProductName());
    }

    private Product createProduct(Long id, String name, BigDecimal price) {
         return new Product(
                name,
                "Описание нового смартфона",
                10,
                new BigDecimal("899.99"),
                price,
                "new_phone.jpg",
                new Discount(new BigDecimal(5)));
    }

}
