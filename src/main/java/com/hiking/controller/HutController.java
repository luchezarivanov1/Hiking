package com.hiking.controller;

import com.hiking.dto.HutDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.service.HutService;
import com.hiking.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/huts")
@RequiredArgsConstructor
public class HutController {

    private final HutService hutService;
    private final PhotoService photoService;

    @GetMapping
    public List<HutDTO> getAllHuts() { return hutService.getAllHuts(); }

    @GetMapping("/{id}")
    public HutDTO getHut(@PathVariable Long id) { return hutService.getById(id); }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public HutDTO createHut(@RequestBody HutDTO dto) { return hutService.create(dto); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HutDTO updateHut(@PathVariable Long id, @RequestBody HutDTO dto) { return hutService.update(id, dto); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteHut(@PathVariable Long id) { hutService.delete(id); }

    @PostMapping("/{id}/photos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhotoInfoDTO> addPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        return ResponseEntity.ok(photoService.addPhoto("huts", id, file, description));
    }

    @DeleteMapping("/{id}/photos/{photoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id, @PathVariable Long photoId) {
        photoService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }
}
