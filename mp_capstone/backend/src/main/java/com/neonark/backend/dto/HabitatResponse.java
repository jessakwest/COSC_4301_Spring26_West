package com.neonark.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class HabitatResponse {
    private Long id;
    private String name;
    private String zone;
    private Integer capacity;
    private Instant createdAt;
}
