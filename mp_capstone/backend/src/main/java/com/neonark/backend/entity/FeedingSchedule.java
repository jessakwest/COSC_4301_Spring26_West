package com.neonark.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalTime;

@Entity
@Table(name="feeding_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedingSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //many schedules can belong to one creature/
    @ManyToOne
    @JoinColumn(name="creature_id", nullable = false)
    private Creature creature;

    @Column(name="feeding_time", nullable = false)
    private LocalTime feedingTime;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name="created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
    }
}
