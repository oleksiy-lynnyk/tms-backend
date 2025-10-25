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
    private List<TestStepDTO> steps;
    private String priority;
    private String state;
    private String type;
    private String component;
    private String automationStatus;
    private String requirement;
    private String owner;
    private String tags;
    private UUID suiteId;
    private UUID projectId;
    private String useCase; // якщо є
}
