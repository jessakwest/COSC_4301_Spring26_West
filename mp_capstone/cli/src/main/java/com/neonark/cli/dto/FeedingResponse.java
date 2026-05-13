package com.neonark.cli.dto;

public class FeedingResponse {
    private Long creatureId;
    private String creatureName;
    private String feedingTime;
    private String habitatName;

    public FeedingResponse(){}

    //getters and settings

    public Long getCreatureId() {
        return creatureId;
    }
    public String getCreatureName() {
        return creatureName;
    }
    public String getFeedingTime() {
        return feedingTime;
    }
    public String getHabitatName() {
        return habitatName;
    }


    public void setCreatureId(Long creatureId) {
        this.creatureId = creatureId;
    }
    public void setCreatureName(String creatureName) {
        this.creatureName = creatureName;
    }
    public void setFeedingTime(String feedingTime) {
        this.feedingTime = feedingTime;
    }
    public void setHabitatName(String habitatName) {
        this.habitatName = habitatName;
    }
}
