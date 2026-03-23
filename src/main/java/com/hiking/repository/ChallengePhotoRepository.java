package com.hiking.repository;

import com.hiking.entity.Challenge;
import com.hiking.entity.ChallengePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengePhotoRepository extends JpaRepository<ChallengePhoto, Long> {
    List<ChallengePhoto> findByChallenge(Challenge challenge);
}
