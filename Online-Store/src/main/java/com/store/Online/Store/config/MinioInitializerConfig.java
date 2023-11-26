package com.store.Online.Store.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@DependsOn("securityConfig")
public class MinioInitializerConfig {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioProperties minioProperties;

    @Bean
    public ApplicationRunner initializeMinio() {
        return args -> {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucketImage()).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucketImage()).build());
            }
            uploadFilesFromFolder();
        };
    }

    @SneakyThrows
    private void uploadFilesFromFolder() {
        try {
            Path folderPath = Paths.get("/app/images"); // Укажите путь к вашей папке
            Files.list(folderPath)
                    .filter(Files::isRegularFile)
                    .forEach(this::uploadFileToMinio);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    private void uploadFileToMinio(Path filePath) {
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            String objectName = filePath.getFileName().toString();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketImage())
                            .object(objectName)
                            .stream(inputStream, inputStream.available(), -1)
                            .build()
            );
        }
    }
}
