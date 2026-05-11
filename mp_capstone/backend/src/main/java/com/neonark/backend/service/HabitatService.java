package com.neonark.backend.service;

import com.neonark.backend.dto.CreateHabitatRequest;
import com.neonark.backend.dto.HabitatResponse;
import com.neonark.backend.entity.Habitat;
import com.neonark.backend.exception.ResourceNotFoundException;
import com.neonark.backend.repository.HabitatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitatService {
    private final HabitatRepository habitatRepository;

    //get all habitats
    public List<HabitatResponse> getAllHabitat() {
        return habitatRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    //get habitat by id
    public HabitatResponse getHabitatById(Long id) {
        Habitat habitat = habitatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitat not found."));
        return mapToResponse(habitat);
    }

    //create new habitat
    public HabitatResponse createHabitat(CreateHabitatRequest request) {
        Habitat habitat = Habitat
                .builder()
                .name(request.getName())
                .zone(request.getZone())
                .capacity(request.getCapacity())
                .build();
        Habitat habitatSaved = habitatRepository.save(habitat);
        return mapToResponse(habitatSaved);
    }

    //dto mapping helpers
    private HabitatResponse mapToResponse(Habitat habitat) {
        return HabitatResponse.builder()
                .id(habitat.getId())
                .name(habitat.getName())
                .zone(habitat.getZone())
                .capacity(habitat.getCapacity())
                .build();
    }
}
