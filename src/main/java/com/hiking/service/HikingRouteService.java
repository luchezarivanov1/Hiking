package com.hiking.service;

import com.hiking.dto.HikingRouteDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.entity.*;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.MountainRepository;
import com.hiking.repository.RoutePhotoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HikingRouteService {

    private final HikingRouteRepository routeRepo;
    private final MountainRepository mountainRepo;
    private final RoutePhotoRepository photoRepo;
    private final FileStorageService fileStorageService;
    private final FavoriteService favoriteService;
    private final ModelMapper mapper;

    public List<HikingRouteDTO> getAllRoutes() {
        return routeRepo.findAll().stream().map(this::mapToDTO).toList();
    }

    public HikingRouteDTO getById(Long id) {
        return mapToDTO(routeRepo.findById(id).orElseThrow(() -> new RuntimeException("Route not found")));
    }

    public HikingRouteDTO create(HikingRouteDTO dto) {
        var route = mapper.map(dto, HikingRoute.class);
        route.setMountain(null);
        if (dto.getMountainId() != null) {
            route.setMountain(mountainRepo.findById(dto.getMountainId())
                    .orElseThrow(() -> new RuntimeException("Mountain not found")));
        }
        return mapToDTO(routeRepo.save(route));
    }

    public HikingRouteDTO update(Long id, HikingRouteDTO dto) {
        var route = routeRepo.findById(id).orElseThrow(() -> new RuntimeException("Route not found"));
        route.setName(dto.getName());
        route.setDistanceKm(dto.getDistanceKm());
        route.setDurationMin(dto.getDurationMin());
        route.setDifficulty(dto.getDifficulty());
        route.setDescription(dto.getDescription());
        route.setMountain(null);
        if (dto.getMountainId() != null) {
            route.setMountain(mountainRepo.findById(dto.getMountainId())
                    .orElseThrow(() -> new RuntimeException("Mountain not found")));
        }
        return mapToDTO(routeRepo.save(route));
    }

    public void delete(Long id) {
        routeRepo.deleteById(id);
    }

    public PhotoInfoDTO addPhoto(Long routeId, MultipartFile file, String description) {
        var route = routeRepo.findById(routeId).orElseThrow(() -> new RuntimeException("Route not found"));
        String url = fileStorageService.store(file, "routes");
        RoutePhoto photo = new RoutePhoto();
        photo.setHikingRoute(route);
        photo.setUrl(url);
        photo.setDescription(description);
        RoutePhoto saved = photoRepo.save(photo);
        return new PhotoInfoDTO(saved.getId(), saved.getUrl());
    }

    public void deletePhoto(Long photoId) {
        photoRepo.deleteById(photoId);
    }

    private HikingRouteDTO mapToDTO(HikingRoute route) {
        var dto = new HikingRouteDTO();
        dto.setId(route.getId());
        dto.setName(route.getName());
        dto.setDistanceKm(route.getDistanceKm());
        dto.setDurationMin(route.getDurationMin());
        dto.setDifficulty(route.getDifficulty());
        dto.setDescription(route.getDescription());
        if (route.getMountain() != null) dto.setMountainId(route.getMountain().getId());
        if (route.getHuts() != null) dto.setHutIds(route.getHuts().stream().map(Hut::getId).toList());
        dto.setPhotos(photoRepo.findByHikingRoute(route).stream()
                .map(p -> new PhotoInfoDTO(p.getId(), p.getUrl())).toList());
        if (route.getWaypoints() != null) dto.setWaypointIds(route.getWaypoints().stream().map(RouteWaypoint::getId).toList());
        dto.setFavorited(favoriteService.isFavorite("routes", route.getId()));
        return dto;
    }
}
