package com.hiking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
