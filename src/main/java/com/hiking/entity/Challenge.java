package com.hiking.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String type; // e.g., "Complete 5 peaks in Rila"

    private Integer targetCount; // number of hikes, peaks, or landmarks

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private Set<ChallengePhoto> photos;

    @ManyToMany(mappedBy = "challenges")
    private List<User> participants = new ArrayList<>();
}
