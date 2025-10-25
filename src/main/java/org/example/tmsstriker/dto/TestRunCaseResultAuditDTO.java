package org.example.tmsstriker.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TestRunCaseResultAuditDTO {
    private UUID id;
    private UUID resultId;
    private UUID changedBy;
    private String changedByName;
    private String oldStatus;
    private String newStatus;
    private String comment;
    private Instant changedAt;
}

