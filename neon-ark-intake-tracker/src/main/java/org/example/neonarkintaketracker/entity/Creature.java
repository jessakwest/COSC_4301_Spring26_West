package org.example.neonarkintaketracker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "creatures")
public class Creature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 120)
    private String species;

    @Column(name = "danger_level", nullable = false, length = 30)
    private String dangerLevel;

    @Column(nullable = false, length = 30)
    private String condition;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(optional = false)
    @JoinColumn(name = "habitat_id", nullable = false)
    private Habitat habitat;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}