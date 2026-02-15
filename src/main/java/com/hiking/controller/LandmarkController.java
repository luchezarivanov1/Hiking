package com.hiking.controller;

import com.hiking.dto.LandmarkDTO;
import com.hiking.entity.LandmarkType;
import com.hiking.service.LandmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/landmarks")
@RequiredArgsConstructor
public class LandmarkController {

    private final LandmarkService landmarkService;

    @GetMapping
    public List<LandmarkDTO> getAllLandmarks() {
        return landmarkService.getAllLandmarks();
    }

    @GetMapping("/{id}")
    public LandmarkDTO getLandmark(@PathVariable Long id) {
        return landmarkService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public LandmarkDTO createLandmark(@RequestBody LandmarkDTO dto) {
        return landmarkService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public LandmarkDTO updateLandmark(@PathVariable Long id, @RequestBody LandmarkDTO dto) {
        return landmarkService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteLandmark(@PathVariable Long id) {
        landmarkService.delete(id);
    }
}
