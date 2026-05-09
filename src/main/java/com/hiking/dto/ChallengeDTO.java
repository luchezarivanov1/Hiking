package com.hiking.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChallengeDTO {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Integer targetCount;
    private List<PhotoInfoDTO> photos;
    private int participantCount;
    private boolean joined;
    private boolean favorited;
}
