package org.example.neonarkintaketracker.entity;

//added so /api/creatures/{id} only shows 1
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "habitats")
public class Habitat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String biome;

    @Column(nullable = false, length = 120)
    private String location;

    @Column(name = "min_temp_c", nullable = false)
    private Integer minTempC;

    @Column(name = "max_temp_c", nullable = false)
    private Integer maxTempC;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // One Habitat -> Many Creatures
    @JsonIgnore //added so /api/creatures/{id} only shows 1
    @OneToMany(mappedBy = "habitat")
    private List<Creature> creatures;
}