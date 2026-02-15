package com.hiking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Landmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private LandmarkType type; // WATERFALL, CAVE, PEAK, RIVER, etc.

    private String description;

    private String location; // GPS coordinates or descriptive

    @ManyToOne
    @JoinColumn(name = "mountain_id", nullable = false)
    private Mountain mountain;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private HikingRoute route; // optional
}
