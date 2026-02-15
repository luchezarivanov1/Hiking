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
    private final ModelMapper modelMapper;

    public List<MountainDTO> getAllMountains() {
        return mountainRepo.findAll().stream()
                .map(m -> modelMapper.map(m, MountainDTO.class))
                .collect(Collectors.toList());
    }

    public MountainDTO getMountainById(Long id) {
        Mountain mountain = mountainRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mountain not found"));
        return modelMapper.map(mountain, MountainDTO.class);
    }

    public MountainDTO createMountain(MountainDTO dto) {
        if (mountainRepo.existsByName(dto.getName())) {
            throw new RuntimeException("Mountain already exists");
        }
        Mountain mountain = modelMapper.map(dto, Mountain.class);
        Mountain saved = mountainRepo.save(mountain);
        return modelMapper.map(saved, MountainDTO.class);
    }
}
