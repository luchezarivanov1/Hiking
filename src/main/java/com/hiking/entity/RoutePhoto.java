package com.hiking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class RoutePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url; // could be cloud URL or local path
    private String description;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private HikingRoute hikingRoute;
}
