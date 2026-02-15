package com.hiking.service;

import com.hiking.dto.HikingRouteDTO;
import com.hiking.entity.HikingRoute;
import com.hiking.repository.HikingRouteRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HikingRouteService {

    private final HikingRouteRepository routeRepo;
    private final ModelMapper modelMapper;

    public List<HikingRouteDTO> getAllRoutes() {
        return routeRepo.findAll().stream()
                .map(route -> modelMapper.map(route, HikingRouteDTO.class))
                .collect(Collectors.toList());
    }

    public HikingRouteDTO getRouteById(Long id) {
        HikingRoute route = routeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));
        return modelMapper.map(route, HikingRouteDTO.class);
    }

    public HikingRouteDTO createRoute(HikingRouteDTO dto) {
        HikingRoute route = modelMapper.map(dto, HikingRoute.class);
        HikingRoute saved = routeRepo.save(route);
        return modelMapper.map(saved, HikingRouteDTO.class);
    }
}
