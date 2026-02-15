package com.hiking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Mountain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String country; // optional if you later expand
    private Integer maxHeight; // meters

    @OneToMany(mappedBy = "mountain", cascade = CascadeType.ALL)
    private Set<Hut> huts;

    @OneToMany(mappedBy = "mountain", cascade = CascadeType.ALL)
    private Set<Review> reviews;
}
