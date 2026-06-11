package com.hiking.controller;

import com.hiking.dto.LandmarkDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.service.LandmarkService;
import com.hiking.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/landmarks")
@RequiredArgsConstructor
public class LandmarkController {

    private final LandmarkService landmarkService;
    private final PhotoService photoService;

    @GetMapping
    public List<LandmarkDTO> getAllLandmarks() { return landmarkService.getAllLandmarks(); }

    @GetMapping("/{id}")
    public LandmarkDTO getLandmark(@PathVariable Long id) { return landmarkService.getById(id); }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public LandmarkDTO createLandmark(@RequestBody LandmarkDTO dto) { return landmarkService.create(dto); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public LandmarkDTO updateLandmark(@PathVariable Long id, @RequestBody LandmarkDTO dto) { return landmarkService.update(id, dto); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteLandmark(@PathVariable Long id) { landmarkService.delete(id); }

    @PostMapping("/{id}/photos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhotoInfoDTO> addPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        return ResponseEntity.ok(photoService.addPhoto("landmarks", id, file, description));
    }

    @DeleteMapping("/{id}/photos/{photoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id, @PathVariable Long photoId) {
        photoService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }
}
