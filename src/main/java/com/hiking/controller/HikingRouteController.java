package com.hiking.controller;

import com.hiking.dto.HikingRouteDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.service.HikingRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class HikingRouteController {

    private final HikingRouteService routeService;

    @GetMapping
    public List<HikingRouteDTO> getAllRoutes() { return routeService.getAllRoutes(); }

    @GetMapping("/{id}")
    public HikingRouteDTO getRoute(@PathVariable Long id) { return routeService.getById(id); }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public HikingRouteDTO createRoute(@RequestBody HikingRouteDTO dto) { return routeService.create(dto); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HikingRouteDTO updateRoute(@PathVariable Long id, @RequestBody HikingRouteDTO dto) { return routeService.update(id, dto); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRoute(@PathVariable Long id) { routeService.delete(id); }

    @PostMapping("/{id}/photos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhotoInfoDTO> addPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        return ResponseEntity.ok(routeService.addPhoto(id, file, description));
    }

    @DeleteMapping("/{id}/photos/{photoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id, @PathVariable Long photoId) {
        routeService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }
}
