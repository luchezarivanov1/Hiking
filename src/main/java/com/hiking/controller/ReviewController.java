package com.hiking.controller;

import com.hiking.dto.ReviewDTO;
import com.hiking.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;

    @GetMapping("/user/{userId}")
    public List<ReviewDTO> getByUser(@PathVariable Long userId) {
        return service.getByUser(userId);
    }

    @GetMapping("/{type}/{id}")
    public List<ReviewDTO> getForEntity(@PathVariable String type, @PathVariable Long id) {
        return service.getForEntity(type, id);
    }

    @GetMapping("/{type}/{id}/summary")
    public Map<String, Object> getSummary(@PathVariable String type, @PathVariable Long id) {
        return service.getSummary(type, id);
    }

    @PostMapping("/{type}/{id}")
    @PreAuthorize("isAuthenticated()")
    public ReviewDTO create(@PathVariable String type, @PathVariable Long id, @RequestBody CreateReviewRequest body) {
        return service.create(type, id, body.rating(), body.comment());
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ReviewDTO update(@PathVariable Long reviewId, @RequestBody CreateReviewRequest body) {
        return service.update(reviewId, body.rating(), body.comment());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    public record CreateReviewRequest(Integer rating, String comment) {}
}
