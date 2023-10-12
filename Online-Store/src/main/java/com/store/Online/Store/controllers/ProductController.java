package com.store.Online.Store.controllers;

import com.store.Online.Store.dto.ProductRequest;
import com.store.Online.Store.service.productService;
import com.store.Online.Store.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final productService productService;

    @Autowired
    public ProductController(productService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction){
        try {
            ProductRequest productRequest = productService.getProductRequestById(productId,direction);
                return ResponseEntity.ok(productRequest);
            } catch (ProductNotFoundException e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> saveProduct(@RequestBody ProductRequest productRequest) {
        productService.saveProduct(productRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody ProductRequest productRequest) {
        productService.updateProduct(productId,productRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }
}
