package com.hiking.dto;

import lombok.Data;

import java.util.List;

@Data
public class LandmarkDTO {
    private Long id;
    private String name;
    private String type; // Peak, Waterfall, Cave, River, Viewpoint
    private Double latitude;
    private Double longitude;
    private String description;
    private Long mountainId;
    private Long hikingRouteId;
    private List<Long> photoIds;
}
