package com.store.Online.Store.service.impl;

import com.store.Online.Store.config.MinioProperties;
import com.store.Online.Store.dto.CommentRequest;
import com.store.Online.Store.entity.Comment;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.entity.User;
import com.store.Online.Store.exception.*;
import com.store.Online.Store.repository.productRepository;
import com.store.Online.Store.repository.userRepository;
import com.store.Online.Store.service.commentService;
import com.store.Online.Store.repository.commentRepository;
import io.minio.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.util.*;

@Service
public class CommentServiceImpl implements commentService{

    private final commentRepository commentRepository;
    private final productRepository productRepository;
    private final userRepository userRepository;

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Autowired
    public CommentServiceImpl(commentRepository commentrepository, productRepository productRepository, userRepository userRepository, MinioClient minioClient, MinioProperties minioProperties){
        this.commentRepository =commentrepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    @Override
    public List<CommentRequest> getCommentsByProductId(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        Product product = productOptional.orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        List<Comment> comments = commentRepository.findByProductId(product);
        return mapToCommentRequestList(comments);
    }

    @Override
    public List<CommentRequest> getCommentsByProductId(Long productId, Sort.Direction direction) {
        Optional<Product> productOptional = productRepository.findById(productId);
        Product product = productOptional.orElseThrow(() -> new ProductNotFoundException("Error id product"));

        Sort sort = Sort.by(direction, "rating");

        List<Comment> comments = commentRepository.findByProductId(product, sort);
        return mapToCommentRequestList(comments);
    }

    @Transactional
    @Override
    public Comment addComment(CommentRequest commentRequest, MultipartFile file) {
        Comment comment = new Comment();

        User user;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            throw new UserNotFoundException("User not found with email " + userEmail);
        }
        comment.setUserId(user);

        Optional<Product> productOptional = productRepository.findById(commentRequest.getProductId());
        if (productOptional.isPresent()) {
            comment.setProductId(productOptional.get());
        } else {
            throw new ProductNotFoundException("Product not found with ID: " + productOptional);
        }

        comment.setText(commentRequest.getText());

        if (commentRequest.getRating() < 1 || commentRequest.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 inclusive.");
        }
        comment.setRating(commentRequest.getRating());

        if (file != null) {
            String fileName =upload(file);
            comment.setImageUrl(fileName);
        }

        try {
            return commentRepository.save(comment);
        } catch (Exception e) {
            throw new CommentAdditionException("Failed to add comment" + e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteImage(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException("Comment not found with ID: " + commentId);
        }

        try {
            commentRepository.deleteImageByCommentId(commentId);
        } catch (Exception e) {
            throw new CommentImageDeletionException("Failed to delete image comment" + e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException("Comment not found with ID: " + commentId);
        }

        try {
            commentRepository.deleteById(commentId);
        } catch (Exception e) {
            throw new CommentDeletionException("Failed to delete comment" + e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteProductComments(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        Product product = productOptional.orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        commentRepository.deleteByProductId(product);
    }
    private List<CommentRequest> mapToCommentRequestList(List<Comment> comments) {
        List<CommentRequest> commentRequests = new ArrayList<>();
        for (Comment comment : comments) {
            commentRequests.add(mapToCommentRequest(comment));
        }
        return commentRequests;
    }

    @Override
    public byte[] getFile(String fileName) {
        try {
            InputStream inputStream;
            inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucketImageComment())
                            .object(fileName)
                            .build());

            return StreamUtils.copyToByteArray(inputStream);
        } catch (Exception e) {
            throw new ImageNotLoadedException("Error get file : " + e.getMessage());
        }
    }

    private CommentRequest mapToCommentRequest(Comment comment) {
        return CommentRequest.builder()
                .firstName(comment.getUserId().getFirstName())
                .text(comment.getText())
                .rating(comment.getRating())
                .imageUrl(comment.getImageUrl())
                .commentId(comment.getCommentId())
                .build();
    }

    @Override
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
                .bucket(minioProperties.getBucketImageComment())
                .build());
        if (!found){
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getBucketImageComment())
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
                .bucket(minioProperties.getBucketImageComment())
                .object(fileName)
                .build());
    }
}
