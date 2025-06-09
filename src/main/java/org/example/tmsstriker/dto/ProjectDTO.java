// ProjectDTO.java
package org.example.tmsstriker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class ProjectDTO {

    private UUID id;

    private String name;

    private String description;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String code;

    private int testCasesCount;
}


