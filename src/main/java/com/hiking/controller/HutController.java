package com.hiking.controller;

import com.hiking.dto.HutDTO;
import com.hiking.service.HutService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/huts")
@RequiredArgsConstructor
public class HutController {

    private final HutService hutService;

    @GetMapping
    public List<HutDTO> getAllHuts() {
        return hutService.getAllHuts();
    }

    @GetMapping("/{id}")
    public HutDTO getHut(@PathVariable Long id) {
        return hutService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public HutDTO createHut(@RequestBody HutDTO dto) {
        return hutService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HutDTO updateHut(@PathVariable Long id, @RequestBody HutDTO dto) {
        return hutService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteHut(@PathVariable Long id) {
        hutService.delete(id);
    }
}
