package com.hiking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Hut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private Integer capacity;
    private Boolean openYearRound;
    @Column(name = "elevation_m")
    private Integer elevationM;
    private Double latitude;
    private Double longitude;
    @Column(name = "has_restaurant")
    private Boolean hasRestaurant;
    @Column(name = "has_accommodation")
    private Boolean hasAccommodation;
    private String phone;

    @ManyToOne
    @JoinColumn(name = "mountain_id")
    private Mountain mountain;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private HikingRoute hikingRoute;

    @OneToMany(mappedBy = "hut", cascade = CascadeType.ALL)
    private Set<Photo> photos;
}
