package com.neonark.backend.repository;

import com.neonark.backend.entity.Creature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreatureRepository extends JpaRepository<Creature, Long> {

    List<Creature> findByRemovedAtIsNull();
    Optional<Creature> findByIdAndRemovedAtIsNull(Long id);
    boolean existsByName(String name);
}
