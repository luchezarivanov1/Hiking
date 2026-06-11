package com.hiking.service;

import com.hiking.dto.PhotoInfoDTO;
import com.hiking.entity.Photo;
import com.hiking.exception.BadRequestException;
import com.hiking.exception.ResourceNotFoundException;
import com.hiking.repository.ChallengeRepository;
import com.hiking.repository.EventRepository;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.HutRepository;
import com.hiking.repository.LandmarkRepository;
import com.hiking.repository.MountainRepository;
import com.hiking.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Centralized photo handling for every entity type. Mirrors {@link ReviewService}:
 * a single polymorphic {@link Photo} table with nullable foreign keys, dispatched
 * by the entity {@code type} string ("routes", "huts", "landmarks", "events",
 * "challenges", "mountains").
 */
@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepo;
    private final FileStorageService fileStorageService;
    private final HikingRouteRepository routeRepo;
    private final HutRepository hutRepo;
    private final LandmarkRepository landmarkRepo;
    private final EventRepository eventRepo;
    private final ChallengeRepository challengeRepo;
    private final MountainRepository mountainRepo;

    public PhotoInfoDTO addPhoto(String type, Long id, MultipartFile file, String description) {
        Photo photo = new Photo();
        attachEntity(photo, type, id);
        photo.setUrl(fileStorageService.store(file, type));
        photo.setDescription(description);
        Photo saved = photoRepo.save(photo);
        return new PhotoInfoDTO(saved.getId(), saved.getUrl());
    }

    public void deletePhoto(Long photoId) {
        photoRepo.deleteById(photoId);
    }

    public List<PhotoInfoDTO> getForEntity(String type, Long id) {
        return findForEntity(type, id).stream()
                .map(p -> new PhotoInfoDTO(p.getId(), p.getUrl()))
                .toList();
    }

    private List<Photo> findForEntity(String type, Long id) {
        return switch (type) {
            case "routes" -> photoRepo.findByHikingRoute_Id(id);
            case "huts" -> photoRepo.findByHut_Id(id);
            case "landmarks" -> photoRepo.findByLandmark_Id(id);
            case "events" -> photoRepo.findByEvent_Id(id);
            case "challenges" -> photoRepo.findByChallenge_Id(id);
            case "mountains" -> photoRepo.findByMountain_Id(id);
            default -> throw new BadRequestException("Photos are not supported for: " + type);
        };
    }

    private void attachEntity(Photo photo, String type, Long id) {
        switch (type) {
            case "routes" -> photo.setHikingRoute(routeRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Route not found")));
            case "huts" -> photo.setHut(hutRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Hut not found")));
            case "landmarks" -> photo.setLandmark(landmarkRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Landmark not found")));
            case "events" -> photo.setEvent(eventRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found")));
            case "challenges" -> photo.setChallenge(challengeRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Challenge not found")));
            case "mountains" -> photo.setMountain(mountainRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Mountain not found")));
            default -> throw new BadRequestException("Photos are not supported for: " + type);
        }
    }
}
