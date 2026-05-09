package com.hiking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long id;
    private String comment;
    private Integer rating; // 1-5
    private Long userId;
    private String username;
    private String userProfileImageUrl;
    private Long routeId;
    private Long hutId;
    private Long landmarkId;
    private Long eventId;
    private String entityType;
    private String entityName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
