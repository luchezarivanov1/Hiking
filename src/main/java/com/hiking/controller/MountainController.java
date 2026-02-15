package com.hiking.controller;

import com.hiking.dto.MountainDTO;
import com.hiking.service.MountainService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mountains")
@RequiredArgsConstructor
public class MountainController {

    private final MountainService mountainService;

    @GetMapping
    public List<MountainDTO> getAllMountains() {
        return mountainService.getAllMountains();
    }

    @GetMapping("/{id}")
    public MountainDTO getMountain(@PathVariable Long id) {
        return mountainService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MountainDTO createMountain(@RequestBody MountainDTO dto) {
        return mountainService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MountainDTO updateMountain(@PathVariable Long id, @RequestBody MountainDTO dto) {
        return mountainService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteMountain(@PathVariable Long id) {
        mountainService.delete(id);
    }
}
