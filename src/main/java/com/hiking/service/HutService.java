package com.hiking.service;

import com.hiking.dto.HutDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.entity.Hut;
import com.hiking.entity.HutPhoto;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.HutPhotoRepository;
import com.hiking.repository.HutRepository;
import com.hiking.repository.MountainRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HutService {

    private final HutRepository hutRepo;
    private final MountainRepository mountainRepo;
    private final HikingRouteRepository routeRepo;
    private final HutPhotoRepository photoRepo;
    private final FileStorageService fileStorageService;
    private final ModelMapper mapper;

    public List<HutDTO> getAllHuts() {
        return hutRepo.findAll().stream().map(this::mapToDTO).toList();
    }

    public HutDTO getById(Long id) {
        return mapToDTO(hutRepo.findById(id).orElseThrow(() -> new RuntimeException("Hut not found")));
    }

    public HutDTO create(HutDTO dto) {
        var hut = mapper.map(dto, Hut.class);
        if (dto.getMountainId() != null) {
            hut.setMountain(mountainRepo.findById(dto.getMountainId())
                    .orElseThrow(() -> new RuntimeException("Mountain not found")));
        }
        if (dto.getRouteId() != null) {
            hut.setHikingRoute(routeRepo.findById(dto.getRouteId())
                    .orElseThrow(() -> new RuntimeException("Route not found")));
        }
        return mapToDTO(hutRepo.save(hut));
    }

    public HutDTO update(Long id, HutDTO dto) {
        var hut = hutRepo.findById(id).orElseThrow(() -> new RuntimeException("Hut not found"));
        hut.setName(dto.getName());
        hut.setAddress(dto.getAddress());
        hut.setCapacity(dto.getCapacity());
        hut.setOpenYearRound(dto.getOpenYearRound());
        hut.setRating(dto.getRating());
        hut.setMountain(null);
        if (dto.getMountainId() != null) {
            hut.setMountain(mountainRepo.findById(dto.getMountainId())
                    .orElseThrow(() -> new RuntimeException("Mountain not found")));
        }
        hut.setHikingRoute(null);
        if (dto.getRouteId() != null) {
            hut.setHikingRoute(routeRepo.findById(dto.getRouteId())
                    .orElseThrow(() -> new RuntimeException("Route not found")));
        }
        return mapToDTO(hutRepo.save(hut));
    }

    public void delete(Long id) {
        hutRepo.deleteById(id);
    }

    public PhotoInfoDTO addPhoto(Long hutId, MultipartFile file, String description) {
        var hut = hutRepo.findById(hutId).orElseThrow(() -> new RuntimeException("Hut not found"));
        String url = fileStorageService.store(file, "huts");
        HutPhoto photo = new HutPhoto();
        photo.setHut(hut);
        photo.setUrl(url);
        photo.setDescription(description);
        HutPhoto saved = photoRepo.save(photo);
        return new PhotoInfoDTO(saved.getId(), saved.getUrl());
    }

    public void deletePhoto(Long photoId) {
        photoRepo.deleteById(photoId);
    }

    private HutDTO mapToDTO(Hut hut) {
        var dto = new HutDTO();
        dto.setId(hut.getId());
        dto.setName(hut.getName());
        dto.setAddress(hut.getAddress());
        dto.setCapacity(hut.getCapacity());
        dto.setOpenYearRound(hut.getOpenYearRound());
        dto.setRating(hut.getRating());
        if (hut.getMountain() != null) dto.setMountainId(hut.getMountain().getId());
        if (hut.getHikingRoute() != null) dto.setRouteId(hut.getHikingRoute().getId());
        dto.setPhotos(photoRepo.findByHut(hut).stream()
                .map(p -> new PhotoInfoDTO(p.getId(), p.getUrl())).toList());
        return dto;
    }
}
