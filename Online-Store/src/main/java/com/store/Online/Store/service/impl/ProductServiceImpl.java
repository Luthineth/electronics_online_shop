package com.store.Online.Store.service.impl;

import com.store.Online.Store.dto.ProductRequest;
import com.store.Online.Store.entity.Discount;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.exception.ProductNotFoundException;
import com.store.Online.Store.repository.productRepository;
import com.store.Online.Store.service.productService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
