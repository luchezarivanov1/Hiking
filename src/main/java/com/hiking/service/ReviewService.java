package com.hiking.service;

import com.hiking.entity.Review;
import com.hiking.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository repo;

    public List<Review> getAll() {
        return repo.findAll();
    }

    public Review create(Review review) {
        return repo.save(review);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
