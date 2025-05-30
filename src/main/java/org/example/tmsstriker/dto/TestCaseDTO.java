// TestCaseDTO.java
package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.UUID;

/** Повний DTO тест-кейсу */
@Data
public class TestCaseDTO {
    private UUID id;
    private String title;
    private String preconditions;
    private String description;
    private String steps;
    private String expectedResult;
    private String priority;
    private String tags;
    private String state;
    private String owner;
    private String type;
    private String automationStatus;
    private String component;
    private String useCase;
    private String requirement;
    /** Посилання на suite та project */
    private UUID suiteId;
    private UUID projectId;
}