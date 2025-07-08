package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class TestSuiteDTO {
    private List<TestSuiteDTO> children;
    private UUID id;
    private UUID projectId;
    private String name;
    private String description;
    private UUID parentId;
    private String code;
}

