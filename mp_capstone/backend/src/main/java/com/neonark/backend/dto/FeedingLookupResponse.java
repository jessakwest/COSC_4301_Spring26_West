package com.neonark.backend.dto;

import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
public class FeedingLookupResponse {
    private Long creatureId;
    private String creatureName;
    private String habitatName;
    private String feedingTime;
}
