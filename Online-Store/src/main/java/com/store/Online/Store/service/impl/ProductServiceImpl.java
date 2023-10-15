package com.store.Online.Store.service.impl;

import com.store.Online.Store.dto.CommentRequest;
import com.store.Online.Store.dto.ProductRequest;
import com.store.Online.Store.entity.Category;
import com.store.Online.Store.entity.Discount;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.entity.ProductCategory;
import com.store.Online.Store.exception.CategoryNotFoundException;
import com.store.Online.Store.exception.DiscountNotFoundException;
import com.store.Online.Store.exception.ProductNotFoundException;
import com.store.Online.Store.repository.*;
import com.store.Online.Store.service.commentService;
import com.store.Online.Store.service.productService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (!optionalProduct.isPresent()) {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }
        return optionalProduct;
    }

    @Override
    public ProductRequest getProductRequestById(Long productId, Sort.Direction direction) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            List<CommentRequest> comments = commentService.getCommentsByProductId(productId, direction);
            ProductRequest productRequest = mapToProductDto(product);
            productRequest.setComments(comments);
            return productRequest;
        }
        throw new ProductNotFoundException("Product with ID " + productId + " not found.");
    }

    @Transactional
    @Override
    public void addProduct(ProductRequest productRequest) {
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
    }

    @Transactional
    @Override
    public void updateProduct(Long productId, ProductRequest productRequest) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
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
        } else {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }
    }

    @Transactional
    @Override
    public void deleteProduct(Long productId) {
        if (productRepository.existsById(productId)) {
            Product product = productRepository.findById(productId).
                    orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found."));

            orderItemRepository.deleteByProductId(product);

            commentService.deleteProductComments(productId);

            productCategoryRepository.deleteByProductId(product);

            productRepository.deleteById(productId);

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

    public ProductRequest mapToProductDto(Product product) {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName(product.getProductName());
        productRequest.setDescription(product.getDescription());
        productRequest.setStockQuantity(product.getStockQuantity());
        productRequest.setPrice(product.getPrice());
        productRequest.setPriceWithDiscount(product.getPriceWithDiscount());
        productRequest.setImageUrl(product.getImageUrl());
        return productRequest;
    }

    private void updatePriceWithDiscount(Product product, BigDecimal discountPercentage) {
        Discount discount = discountRepository.findDiscountsByDiscountPercentage(discountPercentage)
                .orElseThrow(() -> new DiscountNotFoundException("Discount with percentage " + discountPercentage + " not found."));

        BigDecimal originalPrice = product.getPrice();
        BigDecimal discountAmount = originalPrice.multiply(discount.getDiscountPercentage())
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        BigDecimal discountedPrice = originalPrice.subtract(discountAmount);

        product.setDiscountId(discount);
        product.setPriceWithDiscount(discountedPrice);
    }
}
