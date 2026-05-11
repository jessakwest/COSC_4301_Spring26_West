package com.neonark.backend.dto;

import lombok.Getter;
import lombok.Builder;
import java.time.Instant;

@Getter
@Builder
public class ObservationResponse {
    private Long id;
    private String author;
    private String note;
    private Instant createdAt;
}
