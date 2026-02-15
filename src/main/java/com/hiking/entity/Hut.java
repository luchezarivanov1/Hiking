package com.hiking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Hut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "mountain_id")
    private Mountain mountain;

    private String location; // e.g., GPS coordinates
    private Integer beds;
    private Boolean hasRestaurant;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private HikingRoute hikingRoute;
}
