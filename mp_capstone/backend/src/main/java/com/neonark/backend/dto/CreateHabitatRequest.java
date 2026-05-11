package com.neonark.backend.dto;

import lombok.Data;

@Data
public class CreateHabitatRequest   {
    private String name;
    private String zone;
    private Integer capacity;
}
