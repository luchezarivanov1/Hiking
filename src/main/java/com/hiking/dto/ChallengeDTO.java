package com.hiking.dto;

import lombok.Data;

@Data
public class ChallengeDTO {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Integer targetCount;
}
