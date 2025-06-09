// src/main/java/org/example/tmsstriker/dto/ConfigurationDTO.java
package org.example.tmsstriker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class ConfigurationDTO {
    private UUID id;
    private UUID projectId;

    @Schema(description = "Human-readable name")
    private String title;

    @Schema(description = "Unique slug per project")
    private String slug;

    private String description;

    private String os;
    private String browser;
    private String device;
}


