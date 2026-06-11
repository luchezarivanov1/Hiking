package com.hiking.controller;

import com.hiking.dto.MountainDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.service.MountainService;
import com.hiking.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/mountains")
@RequiredArgsConstructor
public class MountainController {

    private final MountainService mountainService;
    private final PhotoService photoService;

    @GetMapping
    public List<MountainDTO> getAllMountains() { return mountainService.getAllMountains(); }

    @GetMapping("/{id}")
    public MountainDTO getMountain(@PathVariable Long id) { return mountainService.getById(id); }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MountainDTO createMountain(@RequestBody MountainDTO dto) { return mountainService.create(dto); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MountainDTO updateMountain(@PathVariable Long id, @RequestBody MountainDTO dto) { return mountainService.update(id, dto); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteMountain(@PathVariable Long id) { mountainService.delete(id); }

    @PostMapping("/{id}/photos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhotoInfoDTO> addPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        return ResponseEntity.ok(photoService.addPhoto("mountains", id, file, description));
    }

    @DeleteMapping("/{id}/photos/{photoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id, @PathVariable Long photoId) {
        photoService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }
}
