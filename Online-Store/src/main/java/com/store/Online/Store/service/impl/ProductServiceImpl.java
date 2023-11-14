package com.store.Online.Store.service.impl;

import com.store.Online.Store.dto.CommentRequest;
import com.store.Online.Store.dto.ProductRequest;
import com.store.Online.Store.entity.Category;
import com.store.Online.Store.entity.Discount;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.entity.ProductCategory;
import com.store.Online.Store.exception.*;
import com.store.Online.Store.repository.*;
import com.store.Online.Store.service.commentService;
import com.store.Online.Store.service.productService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;


import java.io.IOException;
import java.nio.file.*;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements productService {

    private final productRepository productRepository;
    private final productCategoryRepository productCategoryRepository;
    private final categoryRepository categoryRepository;
    private final discountRepository discountRepository;
    private final commentService commentService;
    private final orderItemRepository orderItemRepository;

    @Value("Online-Store/images")
    private String directoryPath;

    @Autowired
    public ProductServiceImpl(productRepository productRepository, productCategoryRepository productCategoryRepository,
                              categoryRepository categoryRepository, discountRepository discountRepository,
                              commentService commentService, orderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.discountRepository = discountRepository;
        this.commentService = commentService;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public Optional<Product> getProductById(Long productId) {
        try {
            return productRepository.findById(productId);
        } catch (Exception e) {
            throw new ProductNotFoundException("Failed to retrieve product with ID: " + productId);
        }
    }

    @Override
    public ProductRequest getProductRequestById(Long productId, Sort.Direction direction) {
        Optional<Product> optionalProduct = getProductById(productId);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            List<CommentRequest> comments = getCommentsForProduct(productId, direction);
            ProductRequest productRequest = mapToProductDto(product);
            productRequest.setComments(comments);
            return productRequest;
        } else {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }
    }

    @Transactional
    @Override
    public void addProduct(ProductRequest productRequest, MultipartFile file) {
        try {
            Product product = createProductFromRequest(productRequest,file);
            productRepository.save(product);
        } catch (Exception e) {
            throw new ProductAdditionException("Failed to add product: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public void updateProduct(Long productId, ProductRequest productRequest, MultipartFile file) {
        Optional<Product> optionalProduct = getProductById(productId);

        if (optionalProduct.isPresent()) {
            try {
                Product product = optionalProduct.get();
                updateProductDetails(product, productRequest,file);
                updateProductCategories(product, productRequest.getCategoryId());
                productRepository.save(product);
            } catch (Exception e) {
                throw new ProductUpdateException("Failed to update product: " + e.getMessage());
            }
        } else {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }
    }

    @Transactional
    @Override
    public void deleteProduct(Long productId) {
        if (productRepository.existsById(productId)) {
            try {
                Product product = getProductById(productId)
                        .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found."));

                orderItemRepository.deleteByProductId(product);
                commentService.deleteProductComments(product.getProductId());
                productCategoryRepository.deleteByProductId(product);
                productRepository.deleteById(product.getProductId());
            } catch (Exception e) {
                throw new ProductDeletionException("Failed to delete product: " + e.getMessage());
            }
        } else {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }
    }

    @Override
    public List<Product> searchProducts(BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, Integer minRating, Sort.Direction price, List<Product> products) {
        List<Product> filteredProducts = new ArrayList<>();

        for (Product product : products) {
            boolean isInRange = isProductInRange(product, minPrice, maxPrice);
            boolean isStockValid = (inStock == null) || (inStock && product.getStockQuantity() > 0);
            boolean isRatingValid = (minRating == null) || (calculateAverageRating(product) != null && calculateAverageRating(product) >= minRating);

            if (isInRange && isStockValid && isRatingValid) {
                filteredProducts.add(product);
            }
        }

        if (price != null) {
            filteredProducts.sort((product1, product2) -> {
                BigDecimal price1 = product1.getPrice();
                BigDecimal price2 = product2.getPrice();
                return price == Sort.Direction.ASC ? price1.compareTo(price2) : price2.compareTo(price1);
            });
        }
        return filteredProducts;
    }

    private List<CommentRequest> getCommentsForProduct(Long productId, Sort.Direction direction) {
        try {
            return commentService.getCommentsByProductId(productId, direction);
        } catch (Exception e) {
            throw new ProductNotFoundException("Failed to retrieve comments for product with ID: " + productId);
        }
    }

    private Product createProductFromRequest(ProductRequest productRequest, MultipartFile image){
        Product product = new Product();
        product.setProductName(productRequest.getProductName());
        product.setDescription(productRequest.getDescription());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setPrice(productRequest.getPrice());
        product.setPriceWithDiscount(productRequest.getPrice());
        if (image == null) {
            product.setImageUrl(directoryPath+"/defaultImage.png");
        } else {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(directoryPath, fileName);

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, filePath);
            } catch (IOException e) {
                throw new ImageNotLoadedException("Image loading error");
            }
            product.setImageUrl(fileName);
        }

        Discount defaultDiscount = new Discount();
        defaultDiscount.setDiscountId(1L);
        product.setDiscountId(defaultDiscount);

        return product;
    }

    private void updateProductDetails(Product product, ProductRequest productRequest, MultipartFile image) throws IOException {
        product.setProductName(productRequest.getProductName());
        product.setDescription(productRequest.getDescription());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setPrice(productRequest.getPrice());

        if (image != null) {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(directoryPath, fileName);
            Files.deleteIfExists(filePath);

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }  catch (IOException e) {
                throw new ImageNotLoadedException("Image loading error");
            }
            product.setImageUrl(fileName);

        }

        updatePriceWithDiscount(product, productRequest.getDiscountPercentage());
    }

    private void updatePriceWithDiscount(Product product, BigDecimal discountPercentage) {
        try {
            Discount discount = discountRepository.findDiscountsByDiscountPercentage(discountPercentage)
                    .orElseThrow(() -> new DiscountNotFoundException("Discount with percentage " + discountPercentage + " not found."));

            BigDecimal originalPrice = product.getPrice();
            BigDecimal discountAmount = originalPrice.multiply(discount.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            BigDecimal discountedPrice = originalPrice.subtract(discountAmount);

            product.setDiscountId(discount);
            product.setPriceWithDiscount(discountedPrice);
        } catch (Exception e) {
            throw new DiscountNotFoundException("Failed to update product price with discount: " + e.getMessage());
        }
    }

    private void updateProductCategories(Product product, List<Long> categoryIds) {
        productCategoryRepository.deleteByProductId(product);

        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + categoryId + " not found."));
            ProductCategory productCategory = new ProductCategory(product, category);
            productCategoryRepository.save(productCategory);
        }
    }

    private boolean isProductInRange(Product product, BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && maxPrice != null) {
            BigDecimal productPrice = product.getPriceWithDiscount();
            return productPrice.compareTo(minPrice) >= 0 && productPrice.compareTo(maxPrice) <= 0;
        }
        return true;
    }

    public Double calculateAverageRating(Product product) {
        Collection<CommentRequest> comments = commentService.getCommentsByProductId(product.getProductId());
        if (comments == null || comments.isEmpty()) {
            return null;
        }

        int sum = 0;
        int count = 0;

        for (CommentRequest comment : comments) {
            if (comment.getRating() != null) {
                sum += comment.getRating();
                count++;
            }
        }

        return count > 0 ? (double) sum / count : null;
    }

    private ProductRequest mapToProductDto(Product product) {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName(product.getProductName());
        productRequest.setDescription(product.getDescription());
        productRequest.setStockQuantity(product.getStockQuantity());
        productRequest.setPrice(product.getPrice());
        productRequest.setPriceWithDiscount(product.getPriceWithDiscount());
        productRequest.setImageUrl(product.getImageUrl());
        return productRequest;
    }

}