/**
 * DTO used for CREATE and UPDATE requests coming from the Java CLI client.
 * NOTE:
 *  - We intentionally EXCLUDE id and createdAt because those are DB-managed.
 *  - This is the "allowed" shape of incoming data.
 */

package org.example.neonarkintaketracker.dto;


//input validations
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreatureRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Species is required")
        String species,

        @NotNull(message = "Danger level is required")
        @Pattern(regexp = "LOW|MEDIUM|HIGH|EXTREME", message = "dangerLevel must be LOW, MEDIUM, HIGH, or EXTREME")
        String dangerLevel,

        @NotNull(message = "Condition is required")
        @Pattern(regexp = "STABLE|RECOVERING|AGGRESSIVE|QUARANTINED", message = "condition must be STABLE, RECOVERING, AGGRESSIVE, or QUARANTINED")
        String condition,

        @NotNull(message = "Habitat ID is required")
        Long habitatId
) {}