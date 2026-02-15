package com.hiking.controller;

import com.hiking.dto.ChallengeDTO;
import com.hiking.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping
    public List<ChallengeDTO> getAllChallenges() {
        return challengeService.getAllChallenges();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDTO> getChallengeById(@PathVariable Long id) {
        return ResponseEntity.ok(challengeService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChallengeDTO> createChallenge(@RequestBody ChallengeDTO dto) {
        return ResponseEntity.ok(challengeService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChallengeDTO> updateChallenge(@PathVariable Long id, @RequestBody ChallengeDTO dto) {
        return ResponseEntity.ok(challengeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        challengeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
