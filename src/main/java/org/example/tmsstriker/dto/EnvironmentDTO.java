package org.example.tmsstriker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class EnvironmentDTO {
    private UUID id;

    private UUID projectId;

    @Schema(description = "Human-readable name")
    private String title;

    @Schema(description = "Unique slug per project")
    private String slug;

    private String description;

    @Schema(description = "Optional host url, like https://staging.example.com")
    private String host;
}
