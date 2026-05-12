package com.neonark.cli.dto;

public class RenameCreatureRequest {
    private Long creatureId;
    private String newName;

    public RenameCreatureRequest() {}
    public RenameCreatureRequest(Long creatureId, String newName) {
        this.creatureId = creatureId;
        this.newName = newName;
    }

    public Long getCreatureId() {
        return creatureId;
    }

    public String getNewName() {
        return newName;
    }
}
