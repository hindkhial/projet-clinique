package com.ehei.radio.service;

import com.ehei.radio.exception.InvalidRadioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class FileValidationService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "dcm", "dicom"
    );

    private static final byte[] JPEG_MAGIC = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_MAGIC = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47};

    public void validateRadioFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRadioException("Fichier est obligatoire");
        }
        validateFileSize(file);
        validateFileExtension(file);
        validateContentType(file);
        validateMagicBytes(file);
        log.info("Fichier valide: {} ({})", file.getOriginalFilename(), formatFileSize(file.getSize()));
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidRadioException(
                    String.format("Fichier trop volumineux: %s (max %s)",
                            formatFileSize(file.getSize()),
                            formatFileSize(MAX_FILE_SIZE))
            );
        }
    }

    private void validateFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            throw new InvalidRadioException("Nom de fichier invalide");
        }
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidRadioException(
                    String.format("Extension non autorisee: .%s (autorisees: %s)",
                            extension, String.join(", ", ALLOWED_EXTENSIONS))
            );
        }
    }

    private void validateContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidRadioException("Le fichier doit etre une image");
        }
    }

    private void validateMagicBytes(MultipartFile file) {
        try {
            byte[] fileHeader = new byte[4];
            file.getInputStream().read(fileHeader);
            if (!isValidImageFile(fileHeader)) {
                throw new InvalidRadioException("Le fichier n'est pas une image valide");
            }
        } catch (IOException e) {
            log.error("Erreur lors de la lecture du fichier: {}", e.getMessage());
            throw new InvalidRadioException("Erreur lors de la validation du fichier");
        }
    }

    private boolean isValidImageFile(byte[] fileHeader) {
        if (fileHeader.length >= 3 &&
                fileHeader[0] == JPEG_MAGIC[0] &&
                fileHeader[1] == JPEG_MAGIC[1] &&
                fileHeader[2] == JPEG_MAGIC[2]) {
            return true;
        }
        if (fileHeader.length >= 4 &&
                fileHeader[0] == PNG_MAGIC[0] &&
                fileHeader[1] == PNG_MAGIC[1] &&
                fileHeader[2] == PNG_MAGIC[2] &&
                fileHeader[3] == PNG_MAGIC[3]) {
            return true;
        }
        return false;
    }

    public String sanitizeFilename(String filename) {
        if (filename == null) return "unknown";
        String sanitized = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (sanitized.length() > 200) {
            String extension = sanitized.contains(".")
                    ? sanitized.substring(sanitized.lastIndexOf("."))
                    : "";
            sanitized = sanitized.substring(0, 200 - extension.length()) + extension;
        }
        return sanitized;
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        else if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        else return String.format("%.2f MB", size / (1024.0 * 1024.0));
    }
}