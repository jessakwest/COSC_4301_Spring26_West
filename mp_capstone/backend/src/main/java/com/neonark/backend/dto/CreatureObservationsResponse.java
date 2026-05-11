package com.neonark.backend.dto;

import lombok.Getter;
import lombok.Builder;
import java.util.List;

@Getter
@Builder
public class CreatureObservationsResponse {
    private Long creatureId;
    private String creatureName;
    private List<ObservationResponse> observations;
}
