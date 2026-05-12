package com.neonark.cli.dto;

import java.util.List;

public class CreatureObservationsResponse {
    private Long creatureId;
    private String creatureName;
    private List<ObservationResponse> observations;

    public CreatureObservationsResponse() {}

    //getters
    public Long getCreatureId() {
        return creatureId;
    }

    public String getCreatureName() {
        return creatureName;
    }

    public List<ObservationResponse> getObservations() {
        return observations;
    }

    //setters
    public void setCreatureId(Long creatureId) {
        this.creatureId = creatureId;
    }
    public void setCreatureName(String creatureName) {
        this.creatureName = creatureName;
    }
    public void setObservations(List<ObservationResponse> observations) {
        this.observations = observations;
    }
}
