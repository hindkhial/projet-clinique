package com.ehei.radio.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public String uploadRadioImage(MultipartFile file, Long radioId, String sanitizedOriginalFilename) {
        try {
            String extension = sanitizedOriginalFilename.contains(".")
                    ? sanitizedOriginalFilename.substring(sanitizedOriginalFilename.lastIndexOf("."))
                    : "";
            String filename = "radio_" + radioId + "_" + UUID.randomUUID() + extension;

            log.info("Uploading radio image: {}", filename);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info("Radio image uploaded successfully: {}", filename);
            return bucketName + "/" + filename;

        } catch (Exception e) {
            log.error("Error uploading radio image: {}", e.getMessage());
            throw new RuntimeException("Failed to upload radio image", e);
        }
    }

    public InputStream downloadRadioImage(String fileUrl) {
        try {
            String filename = fileUrl.substring(fileUrl.indexOf("/") + 1);
            log.info("Downloading radio image: {}", filename);

            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            );

            log.info("Radio image downloaded successfully: {}", filename);
            return stream;

        } catch (Exception e) {
            log.error("Error downloading radio image: {}", e.getMessage());
            throw new RuntimeException("Failed to download radio image", e);
        }
    }

    public void deleteRadioImage(String fileUrl) {
        try {
            String filename = fileUrl.substring(fileUrl.indexOf("/") + 1);
            log.info("Deleting radio image: {}", filename);

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            );

            log.info("Radio image deleted successfully: {}", filename);

        } catch (Exception e) {
            log.error("Error deleting radio image: {}", e.getMessage());
            throw new RuntimeException("Failed to delete radio image", e);
        }
    }
}