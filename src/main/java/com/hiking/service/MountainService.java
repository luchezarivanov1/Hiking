package com.hiking.service;

import com.hiking.dto.MountainDTO;
import com.hiking.entity.Mountain;
import com.hiking.repository.MountainRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MountainService {

    private final MountainRepository mountainRepo;
    private final PhotoService photoService;
    private final FavoriteService favoriteService;
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

    private MountainDTO mapToDTO(Mountain mountain) {
        var dto = new MountainDTO();
        dto.setId(mountain.getId());
        dto.setName(mountain.getName());
        dto.setRegion(mountain.getRegion());
        dto.setHighestPeak(mountain.getHighestPeak());
        dto.setPhotos(photoService.getForEntity("mountains", mountain.getId()));
        dto.setFavorited(favoriteService.isFavorite("mountains", mountain.getId()));
        return dto;
    }
}
