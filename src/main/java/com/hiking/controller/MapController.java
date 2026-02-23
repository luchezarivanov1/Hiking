package com.hiking.controller;

import com.hiking.dto.RouteMapDTO;
import com.hiking.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/maps")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @GetMapping("/route/{routeId}")
    public RouteMapDTO getRoutePolyline(@PathVariable Long routeId) {
        return mapService.getPolyline(routeId);
    }
}
