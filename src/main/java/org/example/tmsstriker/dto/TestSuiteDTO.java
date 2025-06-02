// TestSuiteDTO.java
package org.example.tmsstriker.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestSuiteDTO extends BaseDTO<UUID> {
    private String name;
    private String description;
    private UUID projectId;
    private UUID parentId;
    private List<TestSuiteDTO> children;
    private List<TestCaseDTO> testCases;
    private int testCaseCount; // ← додаємо!
}

