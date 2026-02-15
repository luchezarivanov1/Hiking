package com.hiking.service;

import com.hiking.dto.HutDTO;
import com.hiking.entity.Hut;
import com.hiking.repository.HutRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HutService {

    private final HutRepository hutRepo;
    private final ModelMapper modelMapper;

    public List<HutDTO> getAllHuts() {
        return hutRepo.findAll().stream()
                .map(hut -> modelMapper.map(hut, HutDTO.class))
                .collect(Collectors.toList());
    }

    public List<HutDTO> getHutsByMountain(String mountain) {
        return hutRepo.findByMountain(mountain).stream()
                .map(hut -> modelMapper.map(hut, HutDTO.class))
                .collect(Collectors.toList());
    }

    public HutDTO createHut(HutDTO dto) {
        Hut hut = modelMapper.map(dto, Hut.class);
        Hut saved = hutRepo.save(hut);
        return modelMapper.map(saved, HutDTO.class);
    }
}
