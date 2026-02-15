package com.hiking.dto;

import lombok.Data;

import java.util.List;

@Data
public class HikingRouteDTO {
    private Long id;
    private String name;
    private Long mountainId;
    private Double distanceKm;
    private Integer durationMin;
    private String difficulty;
    private String description;
    private List<Long> hutIds;
    private List<Long> photoIds;
    private List<Long> waypointIds;
}
