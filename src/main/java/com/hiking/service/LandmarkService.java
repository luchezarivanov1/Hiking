package com.hiking.service;

import com.hiking.dto.LandmarkDTO;
import com.hiking.entity.HikingRoute;
import com.hiking.entity.Landmark;
import com.hiking.entity.Mountain;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.LandmarkRepository;
import com.hiking.repository.MountainRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LandmarkService {

    private final LandmarkRepository landmarkRepo;
    private final MountainRepository mountainRepo;
    private final HikingRouteRepository routeRepo;
    private final ModelMapper modelMapper;

    public List<LandmarkDTO> getAllLandmarks() {
        return landmarkRepo.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<LandmarkDTO> getLandmarksByMountain(Long mountainId) {
        Mountain mountain = mountainRepo.findById(mountainId)
                .orElseThrow(() -> new RuntimeException("Mountain not found"));
        return landmarkRepo.findByMountain(mountain).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public LandmarkDTO createLandmark(LandmarkDTO dto) {
        Mountain mountain = mountainRepo.findById(dto.getMountainId())
                .orElseThrow(() -> new RuntimeException("Mountain not found"));

        HikingRoute route = null;
        if (dto.getRouteId() != null) {
            route = routeRepo.findById(dto.getRouteId())
                    .orElseThrow(() -> new RuntimeException("Route not found"));
        }

        Landmark landmark = new Landmark();
        landmark.setName(dto.getName());
        landmark.setType(dto.getType());
        landmark.setDescription(dto.getDescription());
        landmark.setLocation(dto.getLocation());
        landmark.setMountain(mountain);
        landmark.setRoute(route);

        Landmark saved = landmarkRepo.save(landmark);
        return mapToDTO(saved);
    }

    private LandmarkDTO mapToDTO(Landmark landmark) {
        LandmarkDTO dto = modelMapper.map(landmark, LandmarkDTO.class);
        dto.setMountainId(landmark.getMountain().getId());
        if (landmark.getRoute() != null) dto.setRouteId(landmark.getRoute().getId());
        return dto;
    }
}
