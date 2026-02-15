package com.hiking.service;

import com.hiking.dto.LandmarkDTO;
import com.hiking.entity.HikingRoute;
import com.hiking.entity.Landmark;
import com.hiking.entity.LandmarkPhoto;
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
    private final ModelMapper mapper;

    public List<LandmarkDTO> getAllLandmarks() {
        return landmarkRepo.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public LandmarkDTO getById(Long id) {
        var landmark = landmarkRepo.findById(id).orElseThrow(() -> new RuntimeException("Landmark not found"));
        return mapToDTO(landmark);
    }

    public LandmarkDTO create(LandmarkDTO dto) {
        var landmark = mapper.map(dto, Landmark.class);
        if (dto.getMountainId() != null) {
            var mountain = mountainRepo.findById(dto.getMountainId())
                    .orElseThrow(() -> new RuntimeException("Mountain not found"));
            landmark.setMountain(mountain);
        }
        if (dto.getHikingRouteId() != null) {
            var route = routeRepo.findById(dto.getHikingRouteId())
                    .orElseThrow(() -> new RuntimeException("Route not found"));
            landmark.setHikingRoute(route);
        }
        var saved = landmarkRepo.save(landmark);
        return mapToDTO(saved);
    }

    public LandmarkDTO update(Long id, LandmarkDTO dto) {
        var landmark = landmarkRepo.findById(id).orElseThrow(() -> new RuntimeException("Landmark not found"));
        mapper.map(dto, landmark);
        landmark.setId(id);
        if (dto.getMountainId() != null) {
            var mountain = mountainRepo.findById(dto.getMountainId())
                    .orElseThrow(() -> new RuntimeException("Mountain not found"));
            landmark.setMountain(mountain);
        }
        if (dto.getHikingRouteId() != null) {
            var route = routeRepo.findById(dto.getHikingRouteId())
                    .orElseThrow(() -> new RuntimeException("Route not found"));
            landmark.setHikingRoute(route);
        }
        var updated = landmarkRepo.save(landmark);
        return mapToDTO(updated);
    }

    public void delete(Long id) {
        landmarkRepo.deleteById(id);
    }

    private LandmarkDTO mapToDTO(Landmark landmark) {
        var dto = mapper.map(landmark, LandmarkDTO.class);
        if (landmark.getMountain() != null) {
            dto.setMountainId(landmark.getMountain().getId());
        }
        if (landmark.getHikingRoute() != null) {
            dto.setHikingRouteId(landmark.getHikingRoute().getId());
        }
        if (landmark.getPhotos() != null) {
            dto.setPhotoIds(landmark.getPhotos().stream().map(LandmarkPhoto::getId).toList());
        }
        return dto;
    }
}
