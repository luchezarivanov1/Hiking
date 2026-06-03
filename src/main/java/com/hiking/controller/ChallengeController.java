package com.hiking.controller;

import com.hiking.dto.ChallengeDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService service;

    @GetMapping
    public List<ChallengeDTO> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public ChallengeDTO getById(@PathVariable Long id) { return service.getById(id); }

    @GetMapping("/me/joined")
    @PreAuthorize("isAuthenticated()")
    public List<ChallengeDTO> getMyJoined() { return service.getJoinedByCurrentUser(); }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ChallengeDTO create(@RequestBody ChallengeDTO dto) { return service.create(dto); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ChallengeDTO update(@PathVariable Long id, @RequestBody ChallengeDTO dto) { return service.update(id, dto); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { service.delete(id); }

    @PostMapping("/{id}/photos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhotoInfoDTO> addPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        return ResponseEntity.ok(service.addPhoto(id, file, description));
    }

    @DeleteMapping("/{id}/photos/{photoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id, @PathVariable Long photoId) {
        service.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/join")
    @PreAuthorize("isAuthenticated()")
    public ChallengeDTO join(@PathVariable Long id) { return service.join(id); }

    @DeleteMapping("/{id}/join")
    @PreAuthorize("isAuthenticated()")
    public ChallengeDTO leave(@PathVariable Long id) { return service.leave(id); }
}
