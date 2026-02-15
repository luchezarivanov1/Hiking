package com.hiking.dto;

import com.hiking.entity.LandmarkType;
import lombok.Data;

@Data
public class LandmarkDTO {
    private Long id;
    private String name;
    private LandmarkType type;
    private String description;
    private String location;
    private Long mountainId;
    private Long routeId; // optional
}
