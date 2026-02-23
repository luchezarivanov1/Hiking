package com.hiking.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class RouteWaypoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;
    private Double longitude;
    private String description;
    private Integer orderIndex;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private HikingRoute hikingRoute;
}
