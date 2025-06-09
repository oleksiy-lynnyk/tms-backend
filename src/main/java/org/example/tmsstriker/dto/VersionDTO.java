package org.example.tmsstriker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "DTO for version (milestone/release)")
public class VersionDTO {

    @Schema(description = "ID of the version", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5")
    private UUID id;

    @Schema(description = "ID of the project this version belongs to")
    private UUID projectId;

    @Schema(description = "Title of the version", example = "Release 1.0")
    private String title;

    @Schema(description = "Slug - short unique name", example = "release-1-0")
    private String slug;

    @Schema(description = "Description of the version", example = "First major public release")
    private String description;
}
