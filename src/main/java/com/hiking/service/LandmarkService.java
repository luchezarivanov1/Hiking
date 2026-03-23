package com.hiking.service;

import com.hiking.dto.LandmarkDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.entity.HikingRoute;
import com.hiking.entity.Landmark;
import com.hiking.entity.LandmarkPhoto;
import com.hiking.entity.Mountain;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.LandmarkPhotoRepository;
import com.hiking.repository.LandmarkRepository;
import com.hiking.repository.MountainRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LandmarkService {

    private final LandmarkRepository landmarkRepo;
    private final MountainRepository mountainRepo;
    private final HikingRouteRepository routeRepo;
    private final LandmarkPhotoRepository photoRepo;
    private final FileStorageService fileStorageService;
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

    public PhotoInfoDTO addPhoto(Long landmarkId, MultipartFile file, String description) {
        var landmark = landmarkRepo.findById(landmarkId).orElseThrow(() -> new RuntimeException("Landmark not found"));
        String url = fileStorageService.store(file, "landmarks");
        LandmarkPhoto photo = new LandmarkPhoto();
        photo.setLandmark(landmark);
        photo.setUrl(url);
        photo.setDescription(description);
        LandmarkPhoto saved = photoRepo.save(photo);
        return new PhotoInfoDTO(saved.getId(), saved.getUrl());
    }

    public void deletePhoto(Long photoId) {
        photoRepo.deleteById(photoId);
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
        dto.setPhotos(photoRepo.findByLandmark(landmark).stream()
                .map(p -> new PhotoInfoDTO(p.getId(), p.getUrl())).toList());
        return dto;
    }
}
