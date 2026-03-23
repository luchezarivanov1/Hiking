package com.hiking.service;

import com.hiking.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Store an image file under uploads/{subfolder}/ and return its public URL.
     * Reusable for avatars, route photos, event photos, etc.
     *
     * @param file      the uploaded file (must be an image)
     * @param subfolder logical grouping, e.g. "avatars", "routes", "events"
     * @return full public URL, e.g. http://localhost:8080/uploads/avatars/uuid.jpg
     */
    public String store(MultipartFile file, String subfolder) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("Only image files are allowed (JPEG, PNG, GIF, WEBP)");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID() + ext;

        try {
            Path dir = Paths.get(uploadDir, subfolder).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }

        return baseUrl + "/uploads/" + subfolder + "/" + filename;
    }
}
