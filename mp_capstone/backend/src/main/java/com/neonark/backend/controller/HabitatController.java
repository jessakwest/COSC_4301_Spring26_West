package com.neonark.backend.controller;

import com.neonark.backend.dto.HabitatResponse;
import com.neonark.backend.service.HabitatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habitats")
@RequiredArgsConstructor
public class HabitatController {

    private final HabitatService habitatService;

    // GET all habitats
    @GetMapping
    public ResponseEntity<List<HabitatResponse>> getAllHabitats() {
        return ResponseEntity.ok(habitatService.getAllHabitat());
    }

    //GET habitat by id
    @GetMapping("/{id}")
    public ResponseEntity<HabitatResponse> getHabitatById(@PathVariable Long id) {
        return ResponseEntity.ok(habitatService.getHabitatById(id));
    }

}
