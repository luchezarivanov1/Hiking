package com.hiking.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "favorite",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "entity_type", "entity_id"}))
@Data
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "entity_type", nullable = false, length = 32)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;
}
