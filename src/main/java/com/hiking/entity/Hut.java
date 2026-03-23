package com.hiking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
public class Hut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private Integer capacity;
    private Boolean openYearRound;

    @ManyToOne
    @JoinColumn(name = "mountain_id")
    private Mountain mountain;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private HikingRoute hikingRoute;

    private Double rating; // average rating

    @OneToMany(mappedBy = "hut", cascade = CascadeType.ALL)
    private Set<HutPhoto> photos;
}
