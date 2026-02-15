package com.hiking.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;
    private String comment;
    private Integer rating; // 1-5
    private Long userId;
    private Long routeId;
    private Long hutId;
    private Long landmarkId;
}
