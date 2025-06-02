package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class TestCaseDTO {
    private UUID id;
    private String code;
    private String title;
    private String preconditions;
    private String description;
    private String priority;
    private String tags;
    private String state;
    private String owner;
    private String type;
    private String automationStatus;
    private String component;
    private String useCase;
    private String requirement;
    private UUID suiteId;
    private UUID projectId;
    private List<TestStepDTO> steps;
}
