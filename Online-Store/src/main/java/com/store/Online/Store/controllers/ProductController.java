package com.store.Online.Store.controllers;

import com.store.Online.Store.dto.ProductRequest;
import com.store.Online.Store.exception.ProductAdditionException;
import com.store.Online.Store.exception.ProductDeletionException;
import com.store.Online.Store.exception.ProductUpdateException;
import com.store.Online.Store.service.productService;
import com.store.Online.Store.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final productService productService;

    @Value("Online-Store/images")
    private String imageUploadDirectory;

    @Autowired
    private ServletContext servletContext;


    @Autowired
    public ProductController(productService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction){
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
    public ResponseEntity<?> addProduct(@RequestParam("file") MultipartFile file,
                                        @RequestPart("productRequest") ProductRequest productRequest) {
        try {
            if (file != null && !file.isEmpty()) {
                String imageUrl = saveImage(file);
                productRequest.setImageUrl(imageUrl);
            }

            productService.addProduct(productRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (ProductAdditionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    private String saveImage(MultipartFile file) throws IOException {
        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String uploadDirectory = servletContext.getRealPath(imageUploadDirectory);
        String imageUrl = "/images/" + uniqueFileName;

        Path uploadPath = Paths.get(uploadDirectory, uniqueFileName);

        Files.copy(file.getInputStream(), uploadPath);
        return imageUrl;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId,
                                           @RequestParam(name = "file", required = false) MultipartFile file,
                                           @RequestPart("productRequest") ProductRequest productRequest) {
        try {
            if (file != null && !file.isEmpty()) {
                String imageUrl = saveImage(file);
                productRequest.setImageUrl(imageUrl);
            }

            productService.updateProduct(productId, productRequest);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ProductUpdateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.ok().build();
        } catch (ProductDeletionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}
