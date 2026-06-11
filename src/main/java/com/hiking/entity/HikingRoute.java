package com.hiking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HikingRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "mountain_id")
    private Mountain mountain;

    private Double distanceKm;
    private Integer durationMin; // approx
    private String difficulty; // EASY, MEDIUM, HARD

    @Column(length = 2000)
    private String description;

    @OneToMany(mappedBy = "hikingRoute", cascade = CascadeType.ALL)
    private Set<Hut> huts;

    @OneToMany(mappedBy = "hikingRoute", cascade = CascadeType.ALL)
    private Set<Photo> photos;

    @OneToMany(mappedBy = "hikingRoute", cascade = CascadeType.ALL)
    private Set<RouteWaypoint> waypoints;
}
