package com.hiking.dto;

import lombok.Data;

@Data
public class RouteWaypointDTO {
    private Long id;
    private Double latitude;
    private Double longitude;
    private String description;
    private Long routeId;
}
