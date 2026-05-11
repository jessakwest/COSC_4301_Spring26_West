package com.neonark.backend.entity;

//stop spring boot bidirectional relationship recursion problem
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "habitats")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habitat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String zone;

    @Column(nullable = false)
//    @Builder.Default
    private Integer capacity = 10;

    @OneToMany(mappedBy = "habitat")
    @JsonIgnore
    private List<Creature> creatures;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
    }

}
