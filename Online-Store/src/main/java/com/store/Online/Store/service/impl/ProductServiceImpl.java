package com.store.Online.Store.service.impl;

import com.store.Online.Store.dto.CommentRequest;
import com.store.Online.Store.dto.ProductRequest;
import com.store.Online.Store.entity.Comment;
import com.store.Online.Store.entity.Discount;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.exception.ProductNotFoundException;
import com.store.Online.Store.repository.productRepository;
import com.store.Online.Store.service.commentService;
import com.store.Online.Store.service.productService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements productService {

    private final productRepository productRepository;
    private final commentService commentService;

    @Autowired
    public ProductServiceImpl(productRepository productRepository, commentService commentService) {
        this.productRepository = productRepository;
        this.commentService = commentService;
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
            ProductRequest productDto = mapToProductDto(product);
            productDto.setComments(comments);
            return productDto;
        }
        throw new ProductNotFoundException("Product with ID " + productId + " not found.");
    }

    @Override
    public void saveProduct(ProductRequest productRequest) {
        Product product = new Product();
        updateProductAttributes(product, productRequest);
        productRepository.save(product);
    }

    @Override
    public void updateProduct(Long productId, ProductRequest productRequest) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            updateProductAttributes(product, productRequest);
            productRepository.save(product);
        } else {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }
    }

    @Override
    public void deleteProduct(Long productId) {
        if (productRepository.existsById(productId)) {
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

    private void updateProductAttributes(Product product, ProductRequest productRequest) {
        product.setProductName(productRequest.getProductName());
        product.setDescription(productRequest.getDescription());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setPrice(productRequest.getPrice());
        product.setPriceWithDiscount(productRequest.getPriceWithDiscount());
        product.setImageUrl(productRequest.getImageUrl());

        Discount defaultDiscount = new Discount();
        defaultDiscount.setDiscountId(1L);
        product.setDiscountId(defaultDiscount);
    }
    public ProductRequest mapToProductDto(Product product) {
        ProductRequest productDto = new ProductRequest();
        productDto.setProductName(product.getProductName());
        productDto.setDescription(product.getDescription());
        productDto.setStockQuantity(product.getStockQuantity());
        productDto.setPrice(product.getPrice());
        productDto.setPriceWithDiscount(product.getPriceWithDiscount());
        productDto.setImageUrl(product.getImageUrl());
        return productDto;
    }
}
