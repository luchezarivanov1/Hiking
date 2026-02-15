package com.hiking.controller;

import com.hiking.dto.LandmarkDTO;
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

    @GetMapping("/mountain/{mountainId}")
    public List<LandmarkDTO> getLandmarksByMountain(@PathVariable Long mountainId) {
        return landmarkService.getLandmarksByMountain(mountainId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public LandmarkDTO createLandmark(@RequestBody LandmarkDTO dto) {
        return landmarkService.createLandmark(dto);
    }
}
