package com.hiking.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;
    private String comment;
    private Integer rating;
    private Long mountainId;
    private String userEmail;
}
