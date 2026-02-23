package com.hiking.controller;

import com.hiking.entity.RoutePhoto;
import com.hiking.service.RoutePhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/route-photos")
@RequiredArgsConstructor
public class RoutePhotoController {

    private final RoutePhotoService service;

    @GetMapping
    public List<RoutePhoto> getAll() {
        return service.getAll();
    }

    @PostMapping
    public RoutePhoto create(@RequestBody RoutePhoto photo) {
        return service.create(photo);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
