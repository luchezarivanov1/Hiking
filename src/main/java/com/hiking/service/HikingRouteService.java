package com.hiking.service;

import com.hiking.dto.HikingRouteDTO;
import com.hiking.entity.*;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.MountainRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HikingRouteService {

    private final HikingRouteRepository routeRepo;
    private final MountainRepository mountainRepo;
    private final ModelMapper mapper;

    public List<HikingRouteDTO> getAllRoutes() {
        return routeRepo.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public HikingRouteDTO getById(Long id) {
        var route = routeRepo.findById(id).orElseThrow(() -> new RuntimeException("Route not found"));
        return mapToDTO(route);
    }

    public HikingRouteDTO create(HikingRouteDTO dto) {
        var route = mapper.map(dto, HikingRoute.class);
        if (dto.getMountainId() != null) {
            Mountain mountain = mountainRepo.findById(dto.getMountainId())
                    .orElseThrow(() -> new RuntimeException("Mountain not found"));
            route.setMountain(mountain);
        }
        var saved = routeRepo.save(route);
        return mapToDTO(saved);
    }

    public HikingRouteDTO update(Long id, HikingRouteDTO dto) {
        var route = routeRepo.findById(id).orElseThrow(() -> new RuntimeException("Route not found"));
        mapper.map(dto, route);
        route.setId(id);
        if (dto.getMountainId() != null) {
            Mountain mountain = mountainRepo.findById(dto.getMountainId())
                    .orElseThrow(() -> new RuntimeException("Mountain not found"));
            route.setMountain(mountain);
        }
        var updated = routeRepo.save(route);
        return mapToDTO(updated);
    }

    public void delete(Long id) {
        routeRepo.deleteById(id);
    }

    private HikingRouteDTO mapToDTO(HikingRoute route) {
        HikingRouteDTO dto = mapper.map(route, HikingRouteDTO.class);
        if (route.getMountain() != null) {
            dto.setMountainId(route.getMountain().getId());
        }
        if (route.getHuts() != null) {
            dto.setHutIds(route.getHuts().stream().map(Hut::getId).toList());
        }
        if (route.getPhotos() != null) {
            dto.setPhotoIds(route.getPhotos().stream().map(RoutePhoto::getId).toList());
        }
        if (route.getWaypoints() != null) {
            dto.setWaypointIds(route.getWaypoints().stream().map(RouteWaypoint::getId).toList());
        }
        return dto;
    }
}
