package org.example.tmsstriker.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TestRunCaseResultDTO {
    private UUID id;
    private UUID testRunId;
    private UUID testCaseId;
    private String testCaseTitle;
    private String status;
    private String comment;
    private Instant executedAt;
    private UUID executedBy;
    private String executedByName;
}
