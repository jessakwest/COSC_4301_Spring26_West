package com.neonark.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "creatures", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Creature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,  unique = true)
    private String name;

    //lazy fetch prevents unnecessary habitat loading & future serialization issues
    @ManyToOne
    @JoinColumn(name = "habitat_id",  nullable = false)
    private Habitat habitat;

    @Column(nullable = false)
    private String status;

    // null = active
    // not null = soft deleted
    @Column(name = "removed_at")
    private Instant removedAt;

    @Column(name = "created_at",  nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    //automatically set timestamps
    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }
}
