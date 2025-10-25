package org.example.tmsstriker.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class TestRunDTO {
    private UUID id;
    private UUID projectId;
    private String code;
    private String name;
    private String description;
    private String status;
    private Instant startedAt;
    private Instant completedAt;

    private UUID assignedTo;           // id AppUser
    private String assignedToName;     // fullName AppUser

    private List<UUID> testCaseIds;
    private List<UUID> environmentIds;
    private UUID configurationId;
    private UUID versionId;

    // Для деталізації:
    private List<String> environmentNames;
    private String configurationName;
    private String versionName;
    private List<String> testCaseTitles;
}
