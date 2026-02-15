package com.hiking.controller;

import com.hiking.dto.LandmarkPhotoDTO;
import com.hiking.service.LandmarkPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/landmarks/photos")
@RequiredArgsConstructor
public class LandmarkPhotoController {

    private final LandmarkPhotoService photoService;

    @GetMapping("/{landmarkId}")
    public List<LandmarkPhotoDTO> getPhotos(@PathVariable Long landmarkId) {
        return photoService.getPhotosByLandmark(landmarkId);
    }

    @PostMapping("/{landmarkId}")
    @PreAuthorize("hasRole('ADMIN')")
    public LandmarkPhotoDTO addPhoto(@PathVariable Long landmarkId, @RequestBody LandmarkPhotoDTO dto) {
        return photoService.addPhoto(landmarkId, dto);
    }
}
