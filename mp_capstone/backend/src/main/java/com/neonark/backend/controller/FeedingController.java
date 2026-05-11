package com.neonark.backend.controller;

import com.neonark.backend.dto.FeedingLookupResponse;
import com.neonark.backend.service.FeedingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedings")
@RequiredArgsConstructor
public class FeedingController {

    private final FeedingService feedingsService;

    //route 7: GET /api/feedings?time={HH:MM} -- feeding schedules by id
    @GetMapping
    public ResponseEntity<List<FeedingLookupResponse>> getFeedingsByTime(@RequestParam String time) {

        return ResponseEntity.ok(feedingsService.getFeedingsByTime(time));
    }
}
