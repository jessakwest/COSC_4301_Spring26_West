package com.neonark.backend.service;

import com.neonark.backend.dto.*;
import com.neonark.backend.entity.Creature;
import com.neonark.backend.entity.Habitat;
import com.neonark.backend.entity.Observation;
import com.neonark.backend.exception.BadRequestException;
import com.neonark.backend.exception.ConflictException;
import com.neonark.backend.exception.ResourceNotFoundException;
import com.neonark.backend.repository.CreatureRepository;
import com.neonark.backend.repository.HabitatRepository;
import com.neonark.backend.repository.ObservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CreatureService {
    private final CreatureRepository creatureRepository;
    private final HabitatRepository habitatRepository;
    private final ObservationRepository observationRepository;

    //route 1.a and 1.b: GET /api/creatures -- all creatures, including removed or only active
    public List<CreatureResponse> getAllCreatures(boolean includeRemoved) {
        List<Creature> creatures;
        if(includeRemoved) {
            creatures = creatureRepository.findAll();
        } else {
            creatures = creatureRepository.findByRemovedAtIsNull();
        }
        return creatures.stream().map(this::mapToResponse).toList();
    }

    //route 2: GET /api/creaturess/{id}
    public CreatureResponse getCreatureById(Long id) {
        Creature creature = creatureRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Creature not found."));

        return mapToResponse(creature);
    }

    //route 3: POST /api/creatures
    public CreatureResponse createCreature(CreateCreatureRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Creature name is required.");
        }
        if (creatureRepository.existsByName(request.getName())) {
            throw new ConflictException("Creature name already exists.");
        }

        Habitat habitat = habitatRepository.findById(request.getHabitatId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitat not found."));

        Creature creature = Creature.builder()
                .name(request.getName())
                .status(request.getStatus())
                .habitat(habitat)
                .build();

        Creature creatureSaved = creatureRepository.save(creature);

        return mapToResponse(creatureSaved);
    }

    //route 4: PUT /api/creatures/{id}/name
    public CreatureResponse renameCreature(Long id, RenameCreatureRequest request) {

        Creature creature = creatureRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Creature not found."));

        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Creature name is required.");
        }
        if (creatureRepository.existsByName(request.getName())) {
            throw new ConflictException("Creature name already exists.");
        }
        creature.setName(request.getName());
        Creature creatureUpdated = creatureRepository.save(creature);

        return mapToResponse(creatureUpdated);
    }

    //route 5: DELETE /api/creatures/{id}
    public void softDeleteCreature(Long id) {

        Creature creature = creatureRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Creature not found."));

        if (creature.getRemovedAt() != null) {
            throw new ConflictException("Creature not in habitat.");
        }

        creature.setRemovedAt(Instant.now());
        creatureRepository.save(creature);
    }

    //route 6: GET /api/creatures/{id}/observations
    public CreatureObservationsResponse getCreatureObservations(Long id) {
        Creature creature = creatureRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Creature not found."));

        List<ObservationResponse> observationResponses = observationRepository
                .findByCreatureId(id)
                .stream()
                .map(this::mapObservationToResponse)
                .toList();

        return CreatureObservationsResponse.builder()
                .creatureId(creature.getId())
                .creatureName(creature.getName())
                .observations(observationResponses)
                .build();
    }

    // PUT /api/creatures/{id}/restore
    //restore a creature from soft delete (used for internal testing only)
    public CreatureResponse restoreCreature(Long id) {
        Creature creature = creatureRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Creature " + id + ": not found."));

        if(creature.getRemovedAt() == null) {
            throw new ConflictException("Creature is already active.");
        }
        creature.setRemovedAt(null);
        Creature creatureRestored = creatureRepository.save(creature);
        return mapToResponse(creatureRestored);
    }

    //data transfer object(DTO) helper method since code was repetitive
    private CreatureResponse mapToResponse(Creature creature) {
        return CreatureResponse.builder()
                .id(creature.getId())
                .name(creature.getName())
                .status(creature.getStatus())
                .habitatName(creature.getHabitat().getName())
                .createdAt(creature.getCreatedAt())
                .updatedAt(creature.getUpdatedAt())
                .removedAt(creature.getRemovedAt())
                .build();
    }

    private ObservationResponse mapObservationToResponse(Observation observation) {
        return ObservationResponse.builder()
                .id(observation.getId())
                .author(observation.getAuthor().getFullName())
                .note(observation.getNote())
                .createdAt(observation.getCreatedAt())
                .build();
    }

}
