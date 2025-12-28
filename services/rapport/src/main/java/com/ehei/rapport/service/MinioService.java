package com.ehei.rapport.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public String uploadPdf(InputStream pdfStream, Long rapportId) {
        try {

            String filename = "rapport_" + rapportId + "_" + UUID.randomUUID() + ".pdf";

            log.info("Uploading PDF: {}", filename);

            // Upload to MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .stream(pdfStream, -1, 10485760) // 10MB max
                            .contentType("application/pdf")
                            .build()
            );

            log.info("PDF uploaded: {}", filename);

            // Return URL (format: bucket/filename)
            return bucketName + "/" + filename;

        } catch (Exception e) {
            log.error("Error uploading PDF: {}", e.getMessage());
            throw new RuntimeException("Failed to upload PDF", e);
        }
    }

    public InputStream downloadPdf(String fileUrl) {
        try {
            // Extract filename from URL (after the /)
            String filename = fileUrl.substring(fileUrl.indexOf("/") + 1);
            log.info("Downloading PDF: {}", filename);
            // Download from MinIO
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            );

            log.info("PDF downloaded: {}", filename);
            return stream;
        } catch (Exception e) {
            log.error("Error downloading PDF: {}", e.getMessage());
            throw new RuntimeException("Failed to download PDF", e);
        }
    }

    public void deletePdf(String fileUrl) {
        try {
            String filename = fileUrl.substring(fileUrl.indexOf("/") + 1);
            log.info("Deleting PDF: {}", filename);
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            );
            log.info("PDF deleted: {}", filename);
        } catch (Exception e) {
            log.error("Error deleting PDF: {}", e.getMessage());
            throw new RuntimeException("Failed to delete PDF", e);
        }
    }
}
