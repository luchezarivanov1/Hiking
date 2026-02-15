package com.hiking.service;

import com.hiking.dto.LandmarkPhotoDTO;
import com.hiking.entity.Landmark;
import com.hiking.entity.LandmarkPhoto;
import com.hiking.repository.LandmarkPhotoRepository;
import com.hiking.repository.LandmarkRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LandmarkPhotoService {

    private final LandmarkPhotoRepository photoRepo;
    private final LandmarkRepository landmarkRepo;
    private final ModelMapper modelMapper;

    public List<LandmarkPhotoDTO> getPhotosByLandmark(Long landmarkId) {
        Landmark landmark = landmarkRepo.findById(landmarkId)
                .orElseThrow(() -> new RuntimeException("Landmark not found"));

        return photoRepo.findByLandmark(landmark).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public LandmarkPhotoDTO addPhoto(Long landmarkId, LandmarkPhotoDTO dto) {
        Landmark landmark = landmarkRepo.findById(landmarkId)
                .orElseThrow(() -> new RuntimeException("Landmark not found"));

        LandmarkPhoto photo = new LandmarkPhoto();
        photo.setLandmark(landmark);
        photo.setUrl(dto.getUrl());
        photo.setDescription(dto.getDescription());

        LandmarkPhoto saved = photoRepo.save(photo);
        return mapToDTO(saved);
    }

    private LandmarkPhotoDTO mapToDTO(LandmarkPhoto photo) {
        LandmarkPhotoDTO dto = modelMapper.map(photo, LandmarkPhotoDTO.class);
        dto.setLandmarkId(photo.getLandmark().getId());
        return dto;
    }
}
