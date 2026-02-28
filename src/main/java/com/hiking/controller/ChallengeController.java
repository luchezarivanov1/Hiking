package com.hiking.controller;

import com.hiking.entity.Challenge;
import com.hiking.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService service;

    @GetMapping
    public List<Challenge> getAll() {
        return service.getAll();
    }

    @GetMapping("/user/{userId}")
    public List<Challenge> getByUser(@PathVariable Long userId) {
        return service.getChallengesByUserId(userId);
    }

    @PostMapping
    public Challenge create(@RequestBody Challenge c) {
        return service.create(c);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
