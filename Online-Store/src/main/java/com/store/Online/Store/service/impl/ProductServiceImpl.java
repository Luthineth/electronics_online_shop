package com.store.Online.Store.service.impl;

import com.store.Online.Store.dto.ProductRequest;
import com.store.Online.Store.entity.Discount;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.exception.ProductNotFoundException;
import com.store.Online.Store.repository.productRepository;
import com.store.Online.Store.service.productService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class ProductServiceImpl implements productService {

    private final productRepository productRepository;

    @Autowired
    public ProductServiceImpl(productRepository productRepository) {
        this.productRepository = productRepository;
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
    public List<Product> searchProducts(BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, List<Product> products) {
        List<Product> filteredProducts = new ArrayList<>();

        for (Product product : products) {
            boolean isInRange = isProductInRange(product, minPrice, maxPrice);
            boolean isStockValid = (inStock == null) || (inStock && product.getStockQuantity() > 0);

            if (isInRange && isStockValid) {
                filteredProducts.add(product);
            }
        }

        return filteredProducts;
    }

    private boolean isProductInRange(Product product, BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && maxPrice != null) {
            BigDecimal productPrice = product.getPrice();
            return productPrice.compareTo(minPrice) >= 0 && productPrice.compareTo(maxPrice) <= 0;
        }
        return true; // If no price range specified, consider it in range
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
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
}
