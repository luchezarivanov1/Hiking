package com.hiking.service;

import com.hiking.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    private FileStorageService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = new FileStorageService();
        ReflectionTestUtils.setField(service, "uploadDir", tempDir.toString());
        ReflectionTestUtils.setField(service, "baseUrl", "http://localhost:8080");
    }

    @Test
    void store_validImage_writesFileAndReturnsUrl() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "data".getBytes());

        String url = service.store(file, "avatars");

        assertTrue(url.startsWith("http://localhost:8080/uploads/avatars/"));
        assertTrue(url.endsWith(".jpg"));

        Path subDir = tempDir.resolve("avatars");
        try (Stream<Path> files = Files.list(subDir)) {
            assertEquals(1, files.count());
        }
    }

    @Test
    void store_preservesExtensionFromOriginalFilename() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.png", "image/png", "data".getBytes());

        String url = service.store(file, "routes");

        assertTrue(url.endsWith(".png"));
    }

    @Test
    void store_noExtension_returnsUrlWithoutExtension() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "noext", "image/webp", "data".getBytes());

        String url = service.store(file, "huts");

        assertTrue(url.startsWith("http://localhost:8080/uploads/huts/"));
        assertFalse(url.substring(url.lastIndexOf('/') + 1).contains("."));
    }

    @Test
    void store_disallowedContentType_throwsBadRequest() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "data".getBytes());

        assertThrows(BadRequestException.class, () -> service.store(file, "avatars"));
    }

    @Test
    void store_nullContentType_throwsBadRequest() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", null, "data".getBytes());

        assertThrows(BadRequestException.class, () -> service.store(file, "avatars"));
    }
}
