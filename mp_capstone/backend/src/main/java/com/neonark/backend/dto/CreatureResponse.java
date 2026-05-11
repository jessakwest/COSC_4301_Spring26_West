package com.neonark.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CreatureResponse {
    private Long id;
    private String name;
    private String status;
    private String habitatName;
    private Instant createdAt;
    private Instant updatedAt;
}
