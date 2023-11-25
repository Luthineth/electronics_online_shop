package com.store.Online.Store.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String bucketImageComment;
    private String bucketImage;
    private String url;
    private String accessKey;
    private String secretKey;
}
