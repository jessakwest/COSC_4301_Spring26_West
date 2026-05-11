package com.neonark.backend.controller;

import com.neonark.backend.dto.CreateCreatureRequest;
import com.neonark.backend.dto.CreatureObservationsResponse;
import com.neonark.backend.dto.CreatureResponse;
import com.neonark.backend.dto.RenameCreatureRequest;
import com.neonark.backend.service.CreatureService;
import com.neonark.backend.service.ObservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/creatures")
@RequiredArgsConstructor
public class CreatureController {

    private final CreatureService creatureService;

    //route 1.a and 1.b: GET /api/creatures?includeRemoved=true -- get all creatures including removed or only active
    @GetMapping
    public ResponseEntity<List<CreatureResponse>> getAllCreatures(@RequestParam(defaultValue="false") boolean includeRemoved) {
        return ResponseEntity.ok(creatureService.getAllCreatures(includeRemoved));
    }

    //route 2: GET /api/creaturess/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CreatureResponse> getCreatureById(@PathVariable Long id) {
        return ResponseEntity.ok(creatureService.getCreatureById(id));
    }
    //route 3: POST /api/creatures -- creates new creature
    @PostMapping
    public ResponseEntity<CreatureResponse> createCreature(@RequestBody CreateCreatureRequest request) {
        CreatureResponse response = creatureService.createCreature(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //route 4: PUT /api/creatures/{id}/name -- renames creature
    @PutMapping("/{id}/name")
    public ResponseEntity<CreatureResponse> renameCreature(@PathVariable Long id, @RequestBody RenameCreatureRequest request) {
        return ResponseEntity.ok(creatureService.renameCreature(id, request));
    }

    //route 5: DELETE /api/creatures/{id} -- soft delete creature
    @DeleteMapping("/{id}")
    public ResponseEntity<String> softDeleteCreature(@PathVariable Long id) {
       creatureService.softDeleteCreature(id);
        return ResponseEntity.ok("Creature " + id + ": soft deleted.");
    }

    //route 6: GET /api/creatures/{id}/observations -- observations of a creature
    @GetMapping("/{id}/observations")
    public ResponseEntity<CreatureObservationsResponse> getCreatureObservations(@PathVariable Long id) {
     return ResponseEntity.ok(creatureService.getCreatureObservations(id));
    }
}


