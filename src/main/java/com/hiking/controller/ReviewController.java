package com.hiking.controller;

import com.hiking.dto.ReviewDTO;
import com.hiking.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/mountain/{mountainId}")
    public List<ReviewDTO> getReviews(@PathVariable Long mountainId) {
        return reviewService.getReviewsForMountain(mountainId);
    }

    @PostMapping("/mountain/{mountainId}")
    public ReviewDTO addReview(@PathVariable Long mountainId,
                               @RequestBody ReviewDTO dto,
                               Authentication authentication) {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return reviewService.addReview(mountainId, email, dto);
    }
}
