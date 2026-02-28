package com.hiking.service;

import com.hiking.entity.Challenge;
import com.hiking.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository repo;

    public List<Challenge> getAll() {
        return repo.findAll();
    }

    public List<Challenge> getChallengesByUserId(Long userId) {
        return repo.findByUserId(userId);
    }

    public Challenge create(Challenge c) {
        return repo.save(c);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
