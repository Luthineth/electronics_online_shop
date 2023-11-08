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

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ProductServiceImpl implements productService {

    private final productRepository productRepository;
    private final productCategoryRepository productCategoryRepository;
    private final categoryRepository categoryRepository;
    private final discountRepository discountRepository;
    private final commentService commentService;
    private final orderItemRepository orderItemRepository;

    @Value("Online-Store/images")
    private String imageUploadDirectory;
    @Autowired
    public ProductServiceImpl(productRepository productRepository, com.store.Online.Store.repository.productCategoryRepository productCategoryRepository, com.store.Online.Store.repository.categoryRepository categoryRepository, com.store.Online.Store.repository.discountRepository discountRepository, commentService commentService, com.store.Online.Store.repository.orderItemRepository orderItemRepository) {
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
        Optional<Product> optionalProduct;
        try {
            optionalProduct = productRepository.findById(productId);
        } catch (Exception e) {
            throw new ProductNotFoundException("Failed to retrieve product with ID: " + productId);
        }

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            try {
                List<CommentRequest> comments = commentService.getCommentsByProductId(productId, direction);
                ProductRequest productRequest = mapToProductDto(product);
                productRequest.setComments(comments);
                return productRequest;
            } catch (Exception e) {
                throw new ProductNotFoundException("Failed to retrieve comments for product with ID: " + productId);
            }
        } else {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }
    }

    @Transactional
    @Override
    public void addProduct(ProductRequest productRequest) {
        try {
            Product product = new Product();
            product.setProductName(productRequest.getProductName());
            product.setDescription(productRequest.getDescription());
            product.setStockQuantity(productRequest.getStockQuantity());
            product.setPrice(productRequest.getPrice());
            product.setPriceWithDiscount(productRequest.getPrice());
            product.setImageUrl(productRequest.getImageUrl());

            Discount defaultDiscount = new Discount();
            defaultDiscount.setDiscountId(1L);
            product.setDiscountId(defaultDiscount);
            productRepository.save(product);
        } catch (Exception e) {
            throw new ProductAdditionException("Failed to add product: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public void updateProduct(Long productId, ProductRequest productRequest) {
        Optional<Product> optionalProduct;
        try {
            optionalProduct = productRepository.findById(productId);
        } catch (Exception e) {
            throw new ProductNotFoundException("Failed to retrieve product with ID: " + productId);
        }

        if (optionalProduct.isPresent()) {
            try {
                Product product = optionalProduct.get();

                product.setProductName(productRequest.getProductName());
                product.setDescription(productRequest.getDescription());
                product.setStockQuantity(productRequest.getStockQuantity());
                product.setPrice(productRequest.getPrice());
                product.setImageUrl(productRequest.getImageUrl());

                updatePriceWithDiscount(product, productRequest.getDiscountPercentage());


                List<Long> categoryIds = productRequest.getCategoryId();

                if (categoryIds != null) {

                    productCategoryRepository.deleteByProductId(product);

                    for (Long categoryId : categoryIds) {
                        Category category = categoryRepository.findById(categoryId)
                                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + categoryId + " not found."));

                        ProductCategory productCategory = new ProductCategory(product, category);
                        productCategoryRepository.save(productCategory);
                    }
                }


                productRepository.save(product);
            }catch (Exception e){
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
                Product product = productRepository.findById(productId).
                        orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found."));

                orderItemRepository.deleteByProductId(product);

                commentService.deleteProductComments(productId);

                productCategoryRepository.deleteByProductId(product);

                productRepository.deleteById(productId);
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

    public ProductRequest mapToProductDto(Product product) throws IOException {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName(product.getProductName());
        productRequest.setDescription(product.getDescription());
        productRequest.setStockQuantity(product.getStockQuantity());
        productRequest.setPrice(product.getPrice());
        productRequest.setPriceWithDiscount(product.getPriceWithDiscount());
        if (product.getImageUrl() != null) {
            String imageUrl = product.getImageUrl();
            byte[] imageData = Files.readAllBytes(Paths.get(imageUploadDirectory, imageUrl));
            String base64Image = Base64.getEncoder().encodeToString(imageData);
            productRequest.setImageUrl(base64Image);
        }
        return productRequest;
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
}
