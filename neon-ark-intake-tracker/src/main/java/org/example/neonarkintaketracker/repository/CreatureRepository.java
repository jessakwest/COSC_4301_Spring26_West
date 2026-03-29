// CreatureRepository.java

package org.example.neonarkintaketracker.repository;

import org.example.neonarkintaketracker.entity.Creature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Simple CRUD + paging/sorting out of the box
@Repository
public interface CreatureRepository extends JpaRepository<Creature, Long> {
    // No extra methods needed for basic "read" functionality

    // Core CRUD methods you get for free:
    // save(entity)        -> insert or update a creature
    // findById(id)        -> get one creature by primary key
        // findById(id) is already included by JpaRepository
        // It returns Optional<Creature>
    // findAll()           -> get all creatures
    // deleteById(id)      -> delete by primary key
    // delete(entity)      -> delete by passing the entity itself

    // Paging and sorting methods are also included automatically.
}