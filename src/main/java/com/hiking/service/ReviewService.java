package com.hiking.service;

import com.hiking.dto.ReviewDTO;
import com.hiking.entity.*;
import com.hiking.user.entity.User;
import com.hiking.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final UserRepository userRepo;
    private final HikingRouteRepository routeRepo;
    private final HutRepository hutRepo;
    private final LandmarkRepository landmarkRepo;
    private final ModelMapper mapper;

    public List<ReviewDTO> getAllReviews() {
        return reviewRepo.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ReviewDTO getById(Long id) {
        var review = reviewRepo.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
        return mapToDTO(review);
    }

    public ReviewDTO create(ReviewDTO dto) {
        var review = mapper.map(dto, Review.class);
        if (dto.getUserId() != null) {
            User user = userRepo.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            review.setUser(user);
        }
        if (dto.getRouteId() != null) {
            HikingRoute route = routeRepo.findById(dto.getRouteId())
                    .orElseThrow(() -> new RuntimeException("Route not found"));
            review.setHikingRoute(route);
        }
        if (dto.getHutId() != null) {
            Hut hut = hutRepo.findById(dto.getHutId())
                    .orElseThrow(() -> new RuntimeException("Hut not found"));
            review.setHut(hut);
        }
        if (dto.getLandmarkId() != null) {
            Landmark landmark = landmarkRepo.findById(dto.getLandmarkId())
                    .orElseThrow(() -> new RuntimeException("Landmark not found"));
            review.setLandmark(landmark);
        }
        var saved = reviewRepo.save(review);
        return mapToDTO(saved);
    }

    public ReviewDTO update(Long id, ReviewDTO dto) {
        var review = reviewRepo.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
        mapper.map(dto, review);
        review.setId(id);
        if (dto.getUserId() != null) {
            User user = userRepo.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            review.setUser(user);
        }
        // ... set other relations if needed
        var updated = reviewRepo.save(review);
        return mapToDTO(updated);
    }

    public void delete(Long id) {
        reviewRepo.deleteById(id);
    }

    private ReviewDTO mapToDTO(Review review) {
        ReviewDTO dto = mapper.map(review, ReviewDTO.class);
        if (review.getUser() != null) dto.setUserId(review.getUser().getId());
        if (review.getHikingRoute() != null) dto.setRouteId(review.getHikingRoute().getId());
        if (review.getHut() != null) dto.setHutId(review.getHut().getId());
        if (review.getLandmark() != null) dto.setLandmarkId(review.getLandmark().getId());
        return dto;
    }
}
