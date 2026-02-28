package com.hiking.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;
    private Integer rating; // 1-5

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private HikingRoute hikingRoute;

    @ManyToOne
    @JoinColumn(name = "hut_id")
    private Hut hut;

    @ManyToOne
    @JoinColumn(name = "landmark_id")
    private Landmark landmark;
}
