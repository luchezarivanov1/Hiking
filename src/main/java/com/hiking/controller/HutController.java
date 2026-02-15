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

    @GetMapping("/mountain/{name}")
    public List<HutDTO> getHutsByMountain(@PathVariable String name) {
        return hutService.getHutsByMountain(name);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public HutDTO createHut(@RequestBody HutDTO dto) {
        return hutService.createHut(dto);
    }
}
