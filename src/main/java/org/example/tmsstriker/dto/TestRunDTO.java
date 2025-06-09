package org.example.tmsstriker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TestRunDTO {

    private UUID id;

    private UUID projectId;

    private String name;

    private String description;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String code;

    private String status;

    private Instant startedAt;

    private Instant completedAt;

    private UUID assignedTo;
}
