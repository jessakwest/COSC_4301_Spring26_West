package org.example.neonarkintaketracker.repository;

import org.example.neonarkintaketracker.entity.Habitat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitatRepository extends JpaRepository<Habitat, Long> {

    //auto creates:
        // habitatRepository.findById(1L);
        // habitatRepository.findAll();
        // habitatRepository.save(habitat);
}