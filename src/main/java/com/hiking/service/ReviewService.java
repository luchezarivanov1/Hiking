package com.hiking.service;

import com.hiking.dto.ReviewDTO;
import com.hiking.entity.Mountain;
import com.hiking.entity.Review;
import com.hiking.user.entity.User;
import com.hiking.repository.MountainRepository;
import com.hiking.repository.ReviewRepository;
import com.hiking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final MountainRepository mountainRepo;
    private final UserRepository userRepo;
    private final ModelMapper modelMapper;

    public List<ReviewDTO> getReviewsForMountain(Long mountainId) {
        Mountain mountain = mountainRepo.findById(mountainId)
                .orElseThrow(() -> new RuntimeException("Mountain not found"));
        return reviewRepo.findByMountain(mountain).stream()
                .map(r -> {
                    ReviewDTO dto = modelMapper.map(r, ReviewDTO.class);
                    dto.setUserEmail(r.getUser().getEmail());
                    dto.setMountainId(mountain.getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public ReviewDTO addReview(Long mountainId, String userEmail, ReviewDTO dto) {
        Mountain mountain = mountainRepo.findById(mountainId)
                .orElseThrow(() -> new RuntimeException("Mountain not found"));
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = new Review();
        review.setComment(dto.getComment());
        review.setRating(dto.getRating());
        review.setMountain(mountain);
        review.setUser(user);

        Review saved = reviewRepo.save(review);
        dto.setId(saved.getId());
        dto.setMountainId(mountain.getId());
        dto.setUserEmail(user.getEmail());
        return dto;
    }
}
