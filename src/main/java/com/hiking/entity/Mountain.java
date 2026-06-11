package com.hiking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Mountain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String region;
    private Double highestPeak;

    @Column(columnDefinition = "TEXT")
    private String description;
    private Double latitude;
    private Double longitude;

    @OneToMany(mappedBy = "mountain")
    private Set<HikingRoute> routes;

    @OneToMany(mappedBy = "mountain")
    private Set<Hut> huts;

    @OneToMany(mappedBy = "mountain")
    private Set<Landmark> landmarks;

    @OneToMany(mappedBy = "mountain", cascade = CascadeType.ALL)
    private Set<Photo> photos;
}
