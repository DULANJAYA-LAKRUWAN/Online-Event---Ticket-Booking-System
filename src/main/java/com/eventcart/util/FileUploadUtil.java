package com.eventcart.util;

import com.eventcart.exception.ValidationException;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * File upload utility for handling multipart image uploads securely.
 * Enforces file size limits, MIME type verification, extension whitelisting,
 * and path traversal prevention.
 */
public final class FileUploadUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadUtil.class);

    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    public static final String UPLOAD_DIR_NAME = "uploads" + File.separator + "events";

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg",
            ".jpeg",
            ".png",
            ".webp"
    );

    private FileUploadUtil() {
        // Utility class
    }

    /**
     * Uploads an event banner image Part and returns the relative accessible path.
     *
     * @param filePart     The uploaded multipart Part
     * @param appRealPath  The web application context root filesystem path
     * @return Relative web path (e.g., "uploads/events/event_a1b2c3.jpg")
     * @throws ValidationException if the file is invalid, too large, or has disallowed format
     */
    public static String uploadEventBanner(Part filePart, String appRealPath) {
        if (filePart == null || filePart.getSize() == 0) {
            return null; // No file uploaded
        }

        if (filePart.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException("Uploaded image exceeds the maximum permitted size of 5 MB.");
        }

        String mimeType = filePart.getContentType();
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType.toLowerCase())) {
            throw new ValidationException("Invalid image format (" + mimeType + "). Only JPEG, PNG, and WebP are allowed.");
        }

        String submittedFileName = extractFileName(filePart);
        String extension = getFileExtension(submittedFileName);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new ValidationException("Invalid file extension. Only .jpg, .jpeg, .png, and .webp are accepted.");
        }

        // Generate a cryptographically unique safe filename to avoid filename collisions and path traversal
        String safeFileName = "event_" + UUID.randomUUID().toString().replace("-", "") + extension.toLowerCase();

        try {
            // Determine storage directory inside webapp
            Path uploadPath = Paths.get(appRealPath, "uploads", "events").normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Path traversal protection: ensure destination is within uploadPath
            Path destinationFile = uploadPath.resolve(safeFileName).normalize();
            if (!destinationFile.startsWith(uploadPath)) {
                throw new SecurityException("Potential directory traversal attempt detected in filename.");
            }

            try (InputStream inputStream = filePart.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            logger.info("Event banner saved: {}", destinationFile.toAbsolutePath());
            return "uploads/events/" + safeFileName;

        } catch (IOException e) {
            logger.error("Failed to store uploaded file: {}", e.getMessage(), e);
            throw new RuntimeException("Error saving uploaded image. Please try again.", e);
        }
    }

    /**
     * Safely deletes an old image file if replaced.
     */
    public static void deleteOldImage(String relativePath, String appRealPath) {
        if (relativePath == null || !relativePath.startsWith("uploads/events/")) {
            return; // Don't delete external or default asset images
        }
        try {
            Path targetFile = Paths.get(appRealPath, relativePath.replace('/', File.separatorChar)).normalize();
            Path rootUpload = Paths.get(appRealPath, "uploads", "events").normalize();
            if (targetFile.startsWith(rootUpload) && Files.exists(targetFile)) {
                Files.delete(targetFile);
                logger.info("Deleted old event image: {}", targetFile.toAbsolutePath());
            }
        } catch (Exception e) {
            logger.warn("Could not delete old image file {}: {}", relativePath, e.getMessage());
        }
    }

    private static String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp != null) {
            for (String token : contentDisp.split(";")) {
                if (token.trim().startsWith("filename")) {
                    String fileName = token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                    // Strip path components if IE sent full path
                    return Paths.get(fileName).getFileName().toString();
                }
            }
        }
        return "unnamed";
    }

    private static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf('.')).toLowerCase();
    }
}
