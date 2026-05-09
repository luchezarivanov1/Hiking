package com.hiking.service;

import com.hiking.dto.ReviewDTO;
import com.hiking.entity.Event;
import com.hiking.entity.HikingRoute;
import com.hiking.entity.Hut;
import com.hiking.entity.Landmark;
import com.hiking.entity.Review;
import com.hiking.entity.User;
import com.hiking.exception.BadRequestException;
import com.hiking.exception.ResourceNotFoundException;
import com.hiking.exception.UnauthorizedException;
import com.hiking.repository.EventRepository;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.HutRepository;
import com.hiking.repository.LandmarkRepository;
import com.hiking.repository.ReviewRepository;
import com.hiking.repository.UserRepository;
import com.hiking.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository repo;
    private final UserRepository userRepo;
    private final HikingRouteRepository routeRepo;
    private final HutRepository hutRepo;
    private final LandmarkRepository landmarkRepo;
    private final EventRepository eventRepo;

    public List<ReviewDTO> getAll() {
        return repo.findAll().stream().map(this::mapToDTO).toList();
    }

    public List<ReviewDTO> getByUser(Long userId) {
        return repo.findByUser_Id(userId).stream().map(this::mapToDTO).toList();
    }

    public List<ReviewDTO> getForEntity(String type, Long id) {
        return findForEntity(type, id).stream().map(this::mapToDTO).toList();
    }

    public Map<String, Object> getSummary(String type, Long id) {
        List<Review> reviews = findForEntity(type, id);
        double avg = reviews.stream().filter(r -> r.getRating() != null)
                .mapToInt(Review::getRating).average().orElse(0.0);
        return Map.of("average", avg, "count", reviews.size());
    }

    @Transactional
    public ReviewDTO create(String type, Long id, Integer rating, String comment) {
        validateRating(rating);
        User user = currentUserOrThrow();
        Optional<Review> existing = findOwnReview(user.getId(), type, id);
        if (existing.isPresent()) {
            throw new BadRequestException("You have already reviewed this. Edit your existing review instead.");
        }
        Review review = new Review();
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        attachEntity(review, type, id);
        return mapToDTO(repo.save(review));
    }

    @Transactional
    public ReviewDTO update(Long reviewId, Integer rating, String comment) {
        validateRating(rating);
        Review review = repo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        User user = currentUserOrThrow();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && (review.getUser() == null || !review.getUser().getId().equals(user.getId()))) {
            throw new UnauthorizedException("You can only edit your own review");
        }
        review.setRating(rating);
        review.setComment(comment);
        return mapToDTO(repo.save(review));
    }

    @Transactional
    public void delete(Long id) {
        Review review = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        User user = currentUserOrThrow();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && (review.getUser() == null || !review.getUser().getId().equals(user.getId()))) {
            throw new UnauthorizedException("You can only delete your own review");
        }
        repo.delete(review);
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }
    }

    private List<Review> findForEntity(String type, Long id) {
        return switch (type) {
            case "routes" -> repo.findByHikingRoute_IdOrderByIdDesc(id);
            case "huts" -> repo.findByHut_IdOrderByIdDesc(id);
            case "landmarks" -> repo.findByLandmark_IdOrderByIdDesc(id);
            case "events" -> repo.findByEvent_IdOrderByIdDesc(id);
            default -> throw new BadRequestException("Reviews are not supported for: " + type);
        };
    }

    private Optional<Review> findOwnReview(Long userId, String type, Long id) {
        return switch (type) {
            case "routes" -> repo.findByUser_IdAndHikingRoute_Id(userId, id);
            case "huts" -> repo.findByUser_IdAndHut_Id(userId, id);
            case "landmarks" -> repo.findByUser_IdAndLandmark_Id(userId, id);
            case "events" -> repo.findByUser_IdAndEvent_Id(userId, id);
            default -> throw new BadRequestException("Reviews are not supported for: " + type);
        };
    }

    private void attachEntity(Review review, String type, Long id) {
        switch (type) {
            case "routes" -> {
                HikingRoute r = routeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Route not found"));
                review.setHikingRoute(r);
            }
            case "huts" -> {
                Hut h = hutRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hut not found"));
                review.setHut(h);
            }
            case "landmarks" -> {
                Landmark l = landmarkRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Landmark not found"));
                review.setLandmark(l);
            }
            case "events" -> {
                Event e = eventRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
                review.setEvent(e);
            }
            default -> throw new BadRequestException("Reviews are not supported for: " + type);
        }
    }

    private User currentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails details)) {
            throw new UnauthorizedException("Not authenticated");
        }
        return userRepo.findByEmail(details.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private ReviewDTO mapToDTO(Review r) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(r.getId());
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        if (r.getUser() != null) {
            dto.setUserId(r.getUser().getId());
            dto.setUsername(r.getUser().getUsername());
            dto.setUserProfileImageUrl(r.getUser().getProfileImageUrl());
        }
        if (r.getHikingRoute() != null) {
            dto.setRouteId(r.getHikingRoute().getId());
            dto.setEntityType("routes");
            dto.setEntityName(r.getHikingRoute().getName());
        }
        if (r.getHut() != null) {
            dto.setHutId(r.getHut().getId());
            dto.setEntityType("huts");
            dto.setEntityName(r.getHut().getName());
        }
        if (r.getLandmark() != null) {
            dto.setLandmarkId(r.getLandmark().getId());
            dto.setEntityType("landmarks");
            dto.setEntityName(r.getLandmark().getName());
        }
        if (r.getEvent() != null) {
            dto.setEventId(r.getEvent().getId());
            dto.setEntityType("events");
            dto.setEntityName(r.getEvent().getTitle());
        }
        return dto;
    }
}
