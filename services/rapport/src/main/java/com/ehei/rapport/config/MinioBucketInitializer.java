package com.ehei.rapport.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MinioBucketInitializer {

    @Value("${minio.bucket-name}")
    private String bucketName;

    private final MinioClient minioClient;

    @PostConstruct
    public void initBucket(){
        try{
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
                log.info("Bucket '{}' cree avec success", bucketName);
            } else {
                log.info("Bucket '{}' deja exist", bucketName);
            }
        }catch (Exception e){
            log.error("MinIO bucket initializer failed", e);
        }
    }
}
