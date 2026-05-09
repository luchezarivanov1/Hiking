package com.hiking.dto;

import lombok.Data;

import java.util.List;

@Data
public class MountainDTO {
    private Long id;
    private String name;
    private String region;
    private Double highestPeak;
    private String description;
    private Double latitude;
    private Double longitude;
    private List<PhotoInfoDTO> photos;
    private boolean favorited;
}
