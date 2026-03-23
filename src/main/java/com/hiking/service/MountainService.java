package com.hiking.service;

import com.hiking.dto.MountainDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.entity.Mountain;
import com.hiking.entity.MountainPhoto;
import com.hiking.repository.MountainPhotoRepository;
import com.hiking.repository.MountainRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MountainService {

    private final MountainRepository mountainRepo;
    private final MountainPhotoRepository photoRepo;
    private final FileStorageService fileStorageService;
    private final ModelMapper mapper;

    public List<MountainDTO> getAllMountains() {
        return mountainRepo.findAll().stream().map(this::mapToDTO).toList();
    }

    public MountainDTO getById(Long id) {
        return mapToDTO(mountainRepo.findById(id).orElseThrow(() -> new RuntimeException("Mountain not found")));
    }

    public MountainDTO create(MountainDTO dto) {
        var mountain = mapper.map(dto, Mountain.class);
        return mapToDTO(mountainRepo.save(mountain));
    }

    public MountainDTO update(Long id, MountainDTO dto) {
        var mountain = mountainRepo.findById(id).orElseThrow(() -> new RuntimeException("Mountain not found"));
        mountain.setName(dto.getName());
        mountain.setRegion(dto.getRegion());
        mountain.setHighestPeak(dto.getHighestPeak());
        return mapToDTO(mountainRepo.save(mountain));
    }

    public void delete(Long id) {
        mountainRepo.deleteById(id);
    }

    public PhotoInfoDTO addPhoto(Long mountainId, MultipartFile file, String description) {
        var mountain = mountainRepo.findById(mountainId).orElseThrow(() -> new RuntimeException("Mountain not found"));
        String url = fileStorageService.store(file, "mountains");
        MountainPhoto photo = new MountainPhoto();
        photo.setMountain(mountain);
        photo.setUrl(url);
        photo.setDescription(description);
        MountainPhoto saved = photoRepo.save(photo);
        return new PhotoInfoDTO(saved.getId(), saved.getUrl());
    }

    public void deletePhoto(Long photoId) {
        photoRepo.deleteById(photoId);
    }

    private MountainDTO mapToDTO(Mountain mountain) {
        var dto = new MountainDTO();
        dto.setId(mountain.getId());
        dto.setName(mountain.getName());
        dto.setRegion(mountain.getRegion());
        dto.setHighestPeak(mountain.getHighestPeak());
        dto.setPhotos(photoRepo.findByMountain(mountain).stream()
                .map(p -> new PhotoInfoDTO(p.getId(), p.getUrl())).toList());
        return dto;
    }
}
