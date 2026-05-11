package com.neonark.backend.service;

import com.neonark.backend.dto.CreatureObservationsResponse;
import com.neonark.backend.dto.ObservationResponse;
import com.neonark.backend.entity.Creature;
import com.neonark.backend.repository.ObservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ObservationService {
    private final ObservationRepository observationRepository;

    public CreatureObservationsResponse getCreatureObservations(Creature creature) {
        List<ObservationResponse> observationResponses = observationRepository
                .findByCreatureId(creature.getId())
                .stream().map(observation -> ObservationResponse.builder()
                        .id(observation.getId())
                        .author(observation.getAuthor().getFullName())
                        .note(observation.getNote())
                        .createdAt(observation.getCreatedAt())
                        .build()).toList();

        return CreatureObservationsResponse.builder()
                .creatureId(creature.getId())
                .creatureName(creature.getName())
                .observations(observationResponses)
                .build();
    }
}
