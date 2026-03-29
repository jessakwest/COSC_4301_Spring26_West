package org.example.neonarkintaketracker.service;

//for later maybe
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import org.example.neonarkintaketracker.dto.CreatureRequest;
import org.example.neonarkintaketracker.dto.CreatureResponse;

import org.example.neonarkintaketracker.entity.Creature;
import org.example.neonarkintaketracker.entity.Habitat;
import org.example.neonarkintaketracker.repository.CreatureRepository;
import org.example.neonarkintaketracker.repository.HabitatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreatureService {

    private final CreatureRepository repository;

    private final HabitatRepository habitatRepository;
    public CreatureService(CreatureRepository repository, HabitatRepository habitatRepository) {
        this.repository = repository;
        this.habitatRepository = habitatRepository;
    }


    public List<CreatureResponse> getAllCreatures() {
        return repository.findAll()
                .stream()
                .map(creature -> new CreatureResponse(
                        creature.getId(),
                        creature.getName(),
                        creature.getSpecies(),
                        creature.getDangerLevel(),
                        creature.getCondition(),
                        creature.getCreatedAt()
                ))
                .toList();
    }

    public CreatureResponse getById(Long id) {
        Creature creature = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Creature not found"));

        return new CreatureResponse(
                creature.getId(),
                creature.getName(),
                creature.getSpecies(),
                creature.getDangerLevel(),
                creature.getCondition(),
                creature.getCreatedAt()
        );
    }

    public CreatureResponse createCreature(CreatureRequest req) {
        Habitat habitat = habitatRepository.findById(req.habitatId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid habitatId"));

        Creature creature = new Creature();
        creature.setName(req.name());
        creature.setSpecies(req.species());
        creature.setDangerLevel(req.dangerLevel());
        creature.setCondition(req.condition());
        creature.setHabitat(habitat);

        Creature saved = repository.save(creature);

        return new CreatureResponse(
                saved.getId(),
                saved.getName(),
                saved.getSpecies(),
                saved.getDangerLevel(),
                saved.getCondition(),
                saved.getCreatedAt()
        );
    }
}