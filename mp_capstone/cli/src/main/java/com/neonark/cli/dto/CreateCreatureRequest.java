package com.neonark.cli.dto;

public class CreateCreatureRequest {
    private String name;
    private String status;
    private Long habitatId;

    public CreateCreatureRequest() {}
    public CreateCreatureRequest(String name, String status, Long habitatId) {
        this.name = name;
        this.status = status;
        this.habitatId = habitatId;
    }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public Long getHabitatId() { return habitatId; }
}
