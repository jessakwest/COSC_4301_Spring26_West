package com.neonark.cli.dto;

import java.time.Instant;

public class CreatureResponse {
    private Long id;
    private String name;
    private String status;
    private String habitatName;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant removedAt;
    private boolean removed;

    public CreatureResponse() {}

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getStatus() {
        return status;
    }
    public String getHabitatName() {
        return habitatName;
    }

    public Instant getCreatedAt() { return createdAt;}
    public Instant getUpdatedAt() { return updatedAt;}
    public Instant getRemovedAt() { return removedAt;}
    public boolean isRemoved() { return removed; }
}
