package com.hiking.repository;

import com.hiking.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query("SELECT c FROM User u JOIN u.challenges c WHERE u.id = :userId")
    List<Challenge> findByUserId(@Param("userId") Long userId);
}
