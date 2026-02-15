package com.hiking.service;

import com.hiking.dto.LandmarkDTO;
import com.hiking.entity.HikingRoute;
import com.hiking.entity.Landmark;
import com.hiking.entity.LandmarkType;
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

    // Get all landmarks for a mountain with optional filtering
    public List<LandmarkDTO> getLandmarks(Long mountainId, LandmarkType type, String searchName, String searchLocation) {
        Mountain mountain = mountainRepo.findById(mountainId)
                .orElseThrow(() -> new RuntimeException("Mountain not found"));

        List<Landmark> landmarks;

        if (type != null && searchName != null && !searchName.isEmpty()) {
            landmarks = landmarkRepo.findByMountainAndTypeAndNameContainingIgnoreCase(mountain, type, searchName);
        } else if (type != null && searchLocation != null && !searchLocation.isEmpty()) {
            landmarks = landmarkRepo.findByMountainAndTypeAndLocationContainingIgnoreCase(mountain, type, searchLocation);
        } else if (type != null) {
            landmarks = landmarkRepo.findByMountainAndType(mountain, type);
        } else if (searchName != null && !searchName.isEmpty()) {
            landmarks = landmarkRepo.findByMountainAndNameContainingIgnoreCase(mountain, searchName);
        } else if (searchLocation != null && !searchLocation.isEmpty()) {
            landmarks = landmarkRepo.findByMountainAndLocationContainingIgnoreCase(mountain, searchLocation);
        } else {
            landmarks = landmarkRepo.findByMountain(mountain);
        }

        return landmarks.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<LandmarkDTO> getLandmarksByMountain(Long mountainId) {
        return getLandmarks(mountainId, null, null, null);
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
