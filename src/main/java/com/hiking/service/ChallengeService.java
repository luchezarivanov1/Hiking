package com.hiking.service;

import com.hiking.dto.ChallengeDTO;
import com.hiking.entity.Challenge;
import com.hiking.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepo;
    private final ModelMapper mapper;

    public List<ChallengeDTO> getAllChallenges() {
        return challengeRepo.findAll().stream()
                .map(c -> mapper.map(c, ChallengeDTO.class))
                .toList();
    }

    public ChallengeDTO getById(Long id) {
        var challenge = challengeRepo.findById(id).orElseThrow(() -> new RuntimeException("Challenge not found"));
        return mapper.map(challenge, ChallengeDTO.class);
    }

    public ChallengeDTO create(ChallengeDTO dto) {
        var challenge = mapper.map(dto, Challenge.class);
        var saved = challengeRepo.save(challenge);
        return mapper.map(saved, ChallengeDTO.class);
    }

    public ChallengeDTO update(Long id, ChallengeDTO dto) {
        var challenge = challengeRepo.findById(id).orElseThrow(() -> new RuntimeException("Challenge not found"));
        mapper.map(dto, challenge);
        challenge.setId(id);
        var updated = challengeRepo.save(challenge);
        return mapper.map(updated, ChallengeDTO.class);
    }

    public void delete(Long id) {
        challengeRepo.deleteById(id);
    }
}
