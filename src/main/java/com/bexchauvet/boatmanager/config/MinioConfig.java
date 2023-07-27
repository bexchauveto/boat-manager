package com.bexchauvet.boatmanager.config;


import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.url}")
    private String url;
    @Value("${minio.port}")
    private Integer port;
    @Value("${minio.access-key}")
    private String accessKey;
    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient =
                MinioClient.builder()
                        .credentials(accessKey, secretKey)
                        .endpoint(url, port, false)
                        .build();
        return minioClient;
    }

}
