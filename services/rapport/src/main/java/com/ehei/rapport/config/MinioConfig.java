package com.ehei.rapport.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinioConfig {
    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access-key}")
    private String accesseKey;

    @Value("${minio.secret-key}")
    private String secretKey;


    @Bean
    public MinioClient minioClient(){
        log.info("initialisation de MinIO client avec URL: {}", minioUrl);
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accesseKey, secretKey)
                .build();
    }

}
