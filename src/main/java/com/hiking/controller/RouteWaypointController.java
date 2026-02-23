package com.hiking.controller;

import com.hiking.entity.RouteWaypoint;
import com.hiking.service.RouteWaypointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waypoints")
@RequiredArgsConstructor
public class RouteWaypointController {

    private final RouteWaypointService service;

    @GetMapping
    public List<RouteWaypoint> getAll() {
        return service.getAll();
    }

    @PostMapping
    public RouteWaypoint create(@RequestBody RouteWaypoint wp) {
        return service.create(wp);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
