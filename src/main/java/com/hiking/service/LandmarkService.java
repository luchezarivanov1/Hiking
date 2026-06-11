package com.hiking.service;

import com.hiking.dto.LandmarkDTO;
import com.hiking.entity.Landmark;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.LandmarkRepository;
import com.hiking.repository.MountainRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LandmarkService {

    private final LandmarkRepository landmarkRepo;
    private final MountainRepository mountainRepo;
    private final HikingRouteRepository routeRepo;
    private final PhotoService photoService;
    private final FavoriteService favoriteService;
    private final ModelMapper mapper;

    public List<LandmarkDTO> getAllLandmarks() {
        return landmarkRepo.findAll().stream().map(this::mapToDTO).toList();
    }

    public LandmarkDTO getById(Long id) {
        return mapToDTO(landmarkRepo.findById(id).orElseThrow(() -> new RuntimeException("Landmark not found")));
    }

    public LandmarkDTO create(LandmarkDTO dto) {
        var landmark = mapper.map(dto, Landmark.class);
        if (dto.getMountainId() != null) {
            landmark.setMountain(mountainRepo.findById(dto.getMountainId())
                    .orElseThrow(() -> new RuntimeException("Mountain not found")));
        }
        if (dto.getHikingRouteId() != null) {
            landmark.setHikingRoute(routeRepo.findById(dto.getHikingRouteId())
                    .orElseThrow(() -> new RuntimeException("Route not found")));
        }
        return mapToDTO(landmarkRepo.save(landmark));
    }

    public LandmarkDTO update(Long id, LandmarkDTO dto) {
        var landmark = landmarkRepo.findById(id).orElseThrow(() -> new RuntimeException("Landmark not found"));
        landmark.setName(dto.getName());
        landmark.setDescription(dto.getDescription());
        if (dto.getType() != null) {
            try { landmark.setType(com.hiking.entity.LandmarkType.valueOf(dto.getType())); } catch (Exception ignored) {}
        }
        landmark.setLatitude(dto.getLatitude());
        landmark.setLongitude(dto.getLongitude());
        landmark.setMountain(null);
        if (dto.getMountainId() != null) {
            landmark.setMountain(mountainRepo.findById(dto.getMountainId())
                    .orElseThrow(() -> new RuntimeException("Mountain not found")));
        }
        landmark.setHikingRoute(null);
        if (dto.getHikingRouteId() != null) {
            landmark.setHikingRoute(routeRepo.findById(dto.getHikingRouteId())
                    .orElseThrow(() -> new RuntimeException("Route not found")));
        }
        return mapToDTO(landmarkRepo.save(landmark));
    }

    public void delete(Long id) {
        landmarkRepo.deleteById(id);
    }

    private LandmarkDTO mapToDTO(Landmark landmark) {
        var dto = new LandmarkDTO();
        dto.setId(landmark.getId());
        dto.setName(landmark.getName());
        dto.setType(landmark.getType() != null ? landmark.getType().name() : null);
        dto.setLatitude(landmark.getLatitude());
        dto.setLongitude(landmark.getLongitude());
        dto.setDescription(landmark.getDescription());
        if (landmark.getMountain() != null) dto.setMountainId(landmark.getMountain().getId());
        if (landmark.getHikingRoute() != null) dto.setHikingRouteId(landmark.getHikingRoute().getId());
        dto.setPhotos(photoService.getForEntity("landmarks", landmark.getId()));
        dto.setFavorited(favoriteService.isFavorite("landmarks", landmark.getId()));
        return dto;
    }
}
