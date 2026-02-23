package com.hiking.service;

import com.hiking.entity.RoutePhoto;
import com.hiking.repository.RoutePhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutePhotoService {

    private final RoutePhotoRepository repo;

    public List<RoutePhoto> getAll() {
        return repo.findAll();
    }

    public RoutePhoto getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Photo not found"));
    }

    public RoutePhoto create(RoutePhoto photo) {
        return repo.save(photo);
    }

    public RoutePhoto update(Long id, RoutePhoto photo) {
        var existing = getById(id);
        existing.setUrl(photo.getUrl());
        existing.setDescription(photo.getDescription());
        existing.setHikingRoute(photo.getHikingRoute());
        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
