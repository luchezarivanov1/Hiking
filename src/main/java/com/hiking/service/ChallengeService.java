package com.hiking.service;

import com.hiking.dto.ChallengeDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.entity.Challenge;
import com.hiking.entity.ChallengePhoto;
import com.hiking.repository.ChallengePhotoRepository;
import com.hiking.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository repo;
    private final ChallengePhotoRepository photoRepo;
    private final FileStorageService fileStorageService;
    private final ModelMapper mapper;

    public List<ChallengeDTO> getAll() {
        return repo.findAll().stream().map(this::mapToDTO).toList();
    }

    public ChallengeDTO getById(Long id) {
        return mapToDTO(repo.findById(id).orElseThrow(() -> new RuntimeException("Challenge not found")));
    }

    public List<ChallengeDTO> getChallengesByUserId(Long userId) {
        return repo.findByUserId(userId).stream().map(this::mapToDTO).toList();
    }

    public ChallengeDTO create(ChallengeDTO dto) {
        var challenge = mapper.map(dto, Challenge.class);
        return mapToDTO(repo.save(challenge));
    }

    public ChallengeDTO update(Long id, ChallengeDTO dto) {
        var challenge = repo.findById(id).orElseThrow(() -> new RuntimeException("Challenge not found"));
        challenge.setName(dto.getName());
        challenge.setDescription(dto.getDescription());
        challenge.setType(dto.getType());
        challenge.setTargetCount(dto.getTargetCount());
        return mapToDTO(repo.save(challenge));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public PhotoInfoDTO addPhoto(Long challengeId, MultipartFile file, String description) {
        var challenge = repo.findById(challengeId).orElseThrow(() -> new RuntimeException("Challenge not found"));
        String url = fileStorageService.store(file, "challenges");
        ChallengePhoto photo = new ChallengePhoto();
        photo.setChallenge(challenge);
        photo.setUrl(url);
        photo.setDescription(description);
        ChallengePhoto saved = photoRepo.save(photo);
        return new PhotoInfoDTO(saved.getId(), saved.getUrl());
    }

    public void deletePhoto(Long photoId) {
        photoRepo.deleteById(photoId);
    }

    private ChallengeDTO mapToDTO(Challenge challenge) {
        var dto = new ChallengeDTO();
        dto.setId(challenge.getId());
        dto.setName(challenge.getName());
        dto.setDescription(challenge.getDescription());
        dto.setType(challenge.getType());
        dto.setTargetCount(challenge.getTargetCount());
        dto.setPhotos(photoRepo.findByChallenge(challenge).stream()
                .map(p -> new PhotoInfoDTO(p.getId(), p.getUrl())).toList());
        return dto;
    }
}
