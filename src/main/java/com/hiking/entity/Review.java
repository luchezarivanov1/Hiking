package com.hiking.entity;

import com.hiking.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String comment;

    private Integer rating; // 1–5

    @ManyToOne
    @JoinColumn(name = "mountain_id")
    private Mountain mountain;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
