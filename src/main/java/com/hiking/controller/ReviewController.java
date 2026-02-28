package com.hiking.controller;

import com.hiking.entity.Review;
import com.hiking.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;

    @GetMapping
    public List<Review> getAll() {
        return service.getAll();
    }

    @GetMapping("/user/{userId}")
    public List<Review> getByUser(@PathVariable Long userId) {
        return service.getReviewsByUserId(userId);
    }

    @PostMapping
    public Review create(@RequestBody Review review) {
        return service.create(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
