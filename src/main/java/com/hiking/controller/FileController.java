package com.hiking.controller;

import com.hiking.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Generic file upload endpoint.
 * Any authenticated user can upload an image and receive its public URL.
 * Used by profile avatars, route photos, event photos, etc.
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "general") String folder) {
        String url = fileStorageService.store(file, folder);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
