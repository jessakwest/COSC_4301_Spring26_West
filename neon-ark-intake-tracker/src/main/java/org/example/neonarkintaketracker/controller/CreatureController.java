package org.example.neonarkintaketracker.controller;

//maybe for later
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

import jakarta.validation.Valid;
import org.example.neonarkintaketracker.dto.CreatureRequest;
import org.example.neonarkintaketracker.dto.CreatureResponse;
import org.example.neonarkintaketracker.service.CreatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/creatures")
public class CreatureController {

    private final CreatureService service;

    public CreatureController(CreatureService service) {
        this.service = service;
    }

    // GET /api/creatures
    @GetMapping
    public ResponseEntity<List<CreatureResponse>> listAll() {
        return ResponseEntity.ok(service.getAllCreatures());
    }

    // GET /api/creatures/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CreatureResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // POST /api/creatures
    @PostMapping
    public ResponseEntity<CreatureResponse> create(@Valid @RequestBody CreatureRequest req) {
        CreatureResponse created = service.createCreature(req);
        return ResponseEntity.status(201).body(created);
    }
}
