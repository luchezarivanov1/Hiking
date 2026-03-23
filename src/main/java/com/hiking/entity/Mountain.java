package com.hiking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
public class Mountain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String region;
    private Double highestPeak;

    @OneToMany(mappedBy = "mountain")
    private Set<HikingRoute> routes;

    @OneToMany(mappedBy = "mountain")
    private Set<Hut> huts;

    @OneToMany(mappedBy = "mountain")
    private Set<Landmark> landmarks;

    @OneToMany(mappedBy = "mountain", cascade = CascadeType.ALL)
    private Set<MountainPhoto> photos;
}
