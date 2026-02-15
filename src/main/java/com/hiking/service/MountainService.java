package com.hiking.service;

import com.hiking.dto.MountainDTO;
import com.hiking.entity.Mountain;
import com.hiking.repository.MountainRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MountainService {

    private final MountainRepository mountainRepo;
    private final ModelMapper mapper;

    public List<MountainDTO> getAllMountains() {
        return mountainRepo.findAll().stream()
                .map(m -> mapper.map(m, MountainDTO.class))
                .toList();
    }

    public MountainDTO getById(Long id) {
        var mountain = mountainRepo.findById(id).orElseThrow(() -> new RuntimeException("Mountain not found"));
        return mapper.map(mountain, MountainDTO.class);
    }

    public MountainDTO create(MountainDTO dto) {
        var mountain = mapper.map(dto, Mountain.class);
        var saved = mountainRepo.save(mountain);
        return mapper.map(saved, MountainDTO.class);
    }

    public MountainDTO update(Long id, MountainDTO dto) {
        var mountain = mountainRepo.findById(id).orElseThrow(() -> new RuntimeException("Mountain not found"));
        mapper.map(dto, mountain);
        mountain.setId(id);
        var updated = mountainRepo.save(mountain);
        return mapper.map(updated, MountainDTO.class);
    }

    public void delete(Long id) {
        mountainRepo.deleteById(id);
    }
}
