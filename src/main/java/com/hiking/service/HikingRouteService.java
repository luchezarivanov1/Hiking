package com.hiking.service;

import com.hiking.dto.HikingRouteDTO;
import com.hiking.dto.RoutePhotoDTO;
import com.hiking.entity.HikingRoute;
import com.hiking.entity.RoutePhoto;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.RoutePhotoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HikingRouteService {

    private final HikingRouteRepository routeRepo;
    private final RoutePhotoRepository photoRepo;
    private final ModelMapper modelMapper;

    public List<HikingRouteDTO> getAllRoutes() {
        return routeRepo.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public HikingRouteDTO getRouteById(Long id) {
        HikingRoute route = routeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));
        return mapToDTO(route);
    }

    public HikingRouteDTO createRoute(HikingRouteDTO dto) {
        HikingRoute route = modelMapper.map(dto, HikingRoute.class);
        HikingRoute saved = routeRepo.save(route);
        return mapToDTO(saved);
    }

    public HikingRouteDTO updateRoute(Long id, HikingRouteDTO dto) {
        HikingRoute route = routeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        // update fields
        route.setName(dto.getName());
        route.setDistanceKm(dto.getDistanceKm());
        route.setDurationMin(dto.getDurationMin());
        route.setDifficulty(dto.getDifficulty());
        route.setDescription(dto.getDescription());

        HikingRoute updated = routeRepo.save(route);
        return mapToDTO(updated);
    }

    public RoutePhotoDTO addPhoto(Long routeId, RoutePhotoDTO dto) {
        HikingRoute route = routeRepo.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        RoutePhoto photo = new RoutePhoto();
        photo.setHikingRoute(route);
        photo.setUrl(dto.getUrl());
        photo.setDescription(dto.getDescription());

        RoutePhoto saved = photoRepo.save(photo);
        RoutePhotoDTO photoDTO = modelMapper.map(saved, RoutePhotoDTO.class);
        photoDTO.setRouteId(routeId);
        return photoDTO;
    }

    private HikingRouteDTO mapToDTO(HikingRoute route) {
        HikingRouteDTO dto = modelMapper.map(route, HikingRouteDTO.class);
        List<RoutePhotoDTO> photos = photoRepo.findByHikingRoute(route).stream()
                .map(photo -> {
                    RoutePhotoDTO p = modelMapper.map(photo, RoutePhotoDTO.class);
                    p.setRouteId(route.getId());
                    return p;
                })
                .collect(Collectors.toList());
        dto.setPhotos(photos);
        return dto;
    }
}
