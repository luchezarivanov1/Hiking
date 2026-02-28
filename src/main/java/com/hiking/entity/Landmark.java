package com.hiking.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
public class Landmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Enumerated(EnumType.STRING)
    private LandmarkType type; // Peak, Waterfall, Cave, River, Viewpoint
    private Double latitude;
    private Double longitude;
    private String description;

    @ManyToOne
    @JoinColumn(name = "mountain_id")
    private Mountain mountain;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private HikingRoute hikingRoute;

    @OneToMany(mappedBy = "landmark", cascade = CascadeType.ALL)
    private Set<LandmarkPhoto> photos;
}
