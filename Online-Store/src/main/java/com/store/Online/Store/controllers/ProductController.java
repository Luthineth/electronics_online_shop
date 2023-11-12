package com.store.Online.Store.controllers;

import com.store.Online.Store.dto.ProductRequest;
import com.store.Online.Store.exception.ProductAdditionException;
import com.store.Online.Store.exception.ProductDeletionException;
import com.store.Online.Store.exception.ProductUpdateException;
import com.store.Online.Store.service.productService;
import com.store.Online.Store.exception.ProductNotFoundException;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final productService productService;

    @Value("Online-Store/images")
    private String imageUploadDirectory;


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
            Tika tika = new Tika();
            String mimeType = tika.detect(file.getInputStream());
            if (!mimeType.startsWith("image")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file type. Only images are allowed.");
            }

            productService.addProduct(productRequest,file);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (ProductAdditionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId,
                                           @RequestParam(name = "file", required = false) MultipartFile file,
                                           @RequestPart("productRequest") ProductRequest productRequest) {
        try {
            Tika tika = new Tika();
            String mimeType = tika.detect(file.getInputStream());
            if (!mimeType.startsWith("image")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file type. Only images are allowed.");
            }

            productService.updateProduct(productId, productRequest, file);
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

    @GetMapping("/images/{imageName}")
    public ResponseEntity<Resource> serveImage(@PathVariable String imageName) {
        try {
            Path imagePath = Paths.get(imageUploadDirectory, imageName);
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
