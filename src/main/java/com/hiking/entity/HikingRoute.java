package com.hiking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class HikingRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "mountain_id")
    private Mountain mountain;

    private Double distanceKm;
    private Integer durationMin; // approximate duration
    private String difficulty; // e.g., EASY, MEDIUM, HARD

    @Column(length = 2000)
    private String description;

    @OneToMany(mappedBy = "hikingRoute", cascade = CascadeType.ALL)
    private Set<Hut> huts;
}
