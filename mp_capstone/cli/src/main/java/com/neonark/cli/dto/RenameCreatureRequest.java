package com.neonark.cli.dto;

public class RenameCreatureRequest {
    private String name;

    public RenameCreatureRequest() {}
    public RenameCreatureRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
