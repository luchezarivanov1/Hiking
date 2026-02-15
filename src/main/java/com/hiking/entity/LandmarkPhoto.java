package com.hiking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class LandmarkPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url; // image URL or path

    private String description; // optional

    @ManyToOne
    @JoinColumn(name = "landmark_id", nullable = false)
    private Landmark landmark;
}
