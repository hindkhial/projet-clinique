package com.ehei.radio.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinioConfig {

    @Value("${minio.url}")
    private String url;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        log.info("Creation du client MinIO: {}", url);

        MinioClient minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();

        // Initialiser le bucket immediatement apres la creation du client
        initializeBucket(minioClient);

        return minioClient;
    }

    private void initializeBucket(MinioClient minioClient) {
        try {
            log.info("Verification du bucket: {}", bucketName);

            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                log.info("Bucket cree: {}", bucketName);
            } else {
                log.info("Bucket existe deja: {}", bucketName);
            }

        } catch (Exception e) {
            log.error("Erreur lors de la creation du bucket: {}", e.getMessage());
        }
    }
}