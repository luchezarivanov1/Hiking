package com.hiking.service;

import com.hiking.dto.HutDTO;
import com.hiking.entity.Hut;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.HutRepository;
import com.hiking.repository.MountainRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HutService {

    private final HutRepository hutRepo;
    private final MountainRepository mountainRepo;
    private final HikingRouteRepository routeRepo;
    private final ModelMapper mapper;

    public List<HutDTO> getAllHuts() {
        return hutRepo.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public HutDTO getById(Long id) {
        var hut = hutRepo.findById(id).orElseThrow(() -> new RuntimeException("Hut not found"));
        return mapToDTO(hut);
    }

    public HutDTO create(HutDTO dto) {
        var hut = mapper.map(dto, Hut.class);
        if (dto.getMountainId() != null) {
            var mountain = mountainRepo.findById(dto.getMountainId())
                    .orElseThrow(() -> new RuntimeException("Mountain not found"));
            hut.setMountain(mountain);
        }
        if (dto.getRouteId() != null) {
            var route = routeRepo.findById(dto.getRouteId())
                    .orElseThrow(() -> new RuntimeException("Route not found"));
            hut.setHikingRoute(route);
        }
        var saved = hutRepo.save(hut);
        return mapToDTO(saved);
    }

    public HutDTO update(Long id, HutDTO dto) {
        var hut = hutRepo.findById(id).orElseThrow(() -> new RuntimeException("Hut not found"));
        mapper.map(dto, hut);
        hut.setId(id);
        if (dto.getMountainId() != null) {
            var mountain = mountainRepo.findById(dto.getMountainId())
                    .orElseThrow(() -> new RuntimeException("Mountain not found"));
            hut.setMountain(mountain);
        }
        if (dto.getRouteId() != null) {
            var route = routeRepo.findById(dto.getRouteId())
                    .orElseThrow(() -> new RuntimeException("Route not found"));
            hut.setHikingRoute(route);
        }
        var updated = hutRepo.save(hut);
        return mapToDTO(updated);
    }

    public void delete(Long id) {
        hutRepo.deleteById(id);
    }

    private HutDTO mapToDTO(Hut hut) {
        var dto = mapper.map(hut, HutDTO.class);
        if (hut.getMountain() != null) {
            dto.setMountainId(hut.getMountain().getId());
        }
        if (hut.getHikingRoute() != null) {
            dto.setRouteId(hut.getHikingRoute().getId());
        }
        return dto;
    }
}
