package com.neonark.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCreatureRequest {
    private String name;
    private String status;
    private Long habitatId;
}
