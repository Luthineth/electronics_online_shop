package com.store.Online.Store.service.impl;

import com.store.Online.Store.config.MinioProperties;
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
import io.minio.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ProductServiceImpl implements productService {

    private final productRepository productRepository;
    private final productCategoryRepository productCategoryRepository;
    private final categoryRepository categoryRepository;
    private final discountRepository discountRepository;
    private final commentService commentService;
    private final orderItemRepository orderItemRepository;

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Autowired
    public ProductServiceImpl(productRepository productRepository, productCategoryRepository productCategoryRepository,
                              categoryRepository categoryRepository, discountRepository discountRepository,
                              commentService commentService, orderItemRepository orderItemRepository, MinioClient minioClient, MinioProperties minioProperties) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.discountRepository = discountRepository;
        this.commentService = commentService;
        this.orderItemRepository = orderItemRepository;
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    @Override
    public Optional<Product> getProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);

        if (product.isPresent())
            return product;
        else {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
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
    public Product addProduct(ProductRequest productRequest, MultipartFile file) {
        Product product = new Product();
        try {
            product.setProductName(productRequest.getProductName());
            product.setDescription(productRequest.getDescription());
            product.setStockQuantity(productRequest.getStockQuantity());
            product.setPrice(productRequest.getPrice());
            product.setPriceWithDiscount(productRequest.getPrice());
            if (file == null) {
                product.setImageUrl("default.png");
            } else {
               String fileName =upload(file);
               product.setImageUrl(fileName);
            }

            Discount defaultDiscount = new Discount();
            defaultDiscount.setDiscountId(1L);
            product.setDiscountId(defaultDiscount);
            updateProductCategories(product, productRequest.getCategoryId());
            productRepository.save(product);
        } catch (Exception e) {
            throw new ProductAdditionException("Failed to add product: " + e.getMessage());
        }
        return product;
    }

    @Transactional
    @Override
    public Product updateProduct(Long productId, ProductRequest productRequest, MultipartFile file) {
        Optional<Product> optionalProduct = getProductById(productId);

        Product product = null;
        if (optionalProduct.isPresent()) {
            try {
                product = optionalProduct.get();
                updateProductDetails(product, productRequest, file, productId);
                updateProductCategories(product, productRequest.getCategoryId());
                productRepository.save(product);
            } catch (DiscountNotFoundException e) {
                throw new DiscountNotFoundException("Failed to update product price with discount: " + e.getMessage());
            } catch (CategoryNotFoundException e ) {
                throw new CategoryNotFoundException("Category not found: " + e.getMessage());
            }catch (Exception e) {
                throw new ProductUpdateException("Failed to update product: " + e.getMessage());
            }
        } else {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }
        return product;
    }

    @Transactional
    @Override
    public void deleteProduct(Long productId) {
        try {
            Product product = getProductById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found."));
            orderItemRepository.deleteByProductId(product);
            commentService.deleteProductComments(product.getProductId());
            productCategoryRepository.deleteByProductId(product);
            productRepository.deleteById(product.getProductId());
        } catch (ProductNotFoundException e){
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        } catch (Exception e) {
            throw new ProductDeletionException("Failed to delete product: " + e.getMessage());
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

    @Override
    public byte[] getFile(String fileName) {
        try {
            InputStream inputStream;
            inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucketImage())
                            .object(fileName)
                            .build());

            return StreamUtils.copyToByteArray(inputStream);
        } catch (Exception e) {
            throw new ImageNotLoadedException("Error get file : " + e.getMessage());
        }
    }

    private List<CommentRequest> getCommentsForProduct(Long productId, Sort.Direction direction) {
        try {
            return commentService.getCommentsByProductId(productId, direction);
        } catch (Exception e) {
            throw new ProductNotFoundException("Failed to retrieve comments for product with ID: " + productId);
        }
    }

    private void updateProductDetails(Product product, ProductRequest productRequest, MultipartFile image,Long productId){
        product.setProductName(productRequest.getProductName());
        product.setDescription(productRequest.getDescription());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setPrice(productRequest.getPrice());

        if (image != null && !image.isEmpty()) {
            String fileName = upload(image);
            product.setImageUrl(fileName);
        } else
            product.setImageUrl(productRepository.findImageUrlByProductId(productId));

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
        if(product.getProductId() != null)
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

    public String upload (MultipartFile file) {
        try {
            createBucket();
        } catch (Exception e) {
            throw new ImageNotLoadedException("Image upload failed" + e.getMessage());
        }
        if (file.isEmpty() && file.getOriginalFilename() == null){
            throw new ImageNotLoadedException("Image must have name.");
        }
        String fileName = generateFileName(file);
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (Exception e){
            throw new ImageNotLoadedException("Image upload failed" + e.getMessage());
        }
        saveImage(inputStream,fileName);
        return fileName;
    }

    @SneakyThrows
    private void createBucket(){
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.getBucketImage())
                .build());
        if (!found){
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getBucketImage())
                    .build());
        }
    }

    private String generateFileName(MultipartFile file) {
        String extension = getExtension(file);
        return UUID.randomUUID() +"." +extension;
    }

    private String getExtension(MultipartFile file){
        return  Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf(".")+1);
    }

    @SneakyThrows
    private void saveImage(InputStream inputStream, String fileName){
        minioClient.putObject(PutObjectArgs.builder()
                .stream(inputStream, inputStream.available(), -1)
                .bucket(minioProperties.getBucketImage())
                .object(fileName)
                .build());
    }
}
