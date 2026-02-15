package com.hiking.controller;

import com.hiking.dto.HikingRouteDTO;
import com.hiking.dto.RoutePhotoDTO;
import com.hiking.service.HikingRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class HikingRouteController {

    private final HikingRouteService routeService;

    @GetMapping
    public List<HikingRouteDTO> getAllRoutes() {
        return routeService.getAllRoutes();
    }

    @GetMapping("/{id}")
    public HikingRouteDTO getRoute(@PathVariable Long id) {
        return routeService.getRouteById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public HikingRouteDTO createRoute(@RequestBody HikingRouteDTO dto) {
        return routeService.createRoute(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HikingRouteDTO updateRoute(@PathVariable Long id, @RequestBody HikingRouteDTO dto) {
        return routeService.updateRoute(id, dto);
    }

    @PostMapping("/{id}/photos")
    @PreAuthorize("hasRole('ADMIN')")
    public RoutePhotoDTO addPhoto(@PathVariable Long id, @RequestBody RoutePhotoDTO dto) {
        return routeService.addPhoto(id, dto);
    }
}
