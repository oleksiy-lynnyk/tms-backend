// TestSuiteDTO.java
package org.example.tmsstriker.dto;

import java.util.List;
import java.util.UUID;

public class TestSuiteDTO {
    private UUID id;
    private UUID projectId;
    private UUID parentId;
    private String name;
    private String description;
    private String code;
    // --- ДОДАТИ поле для дерева ---
    private List<TestSuiteDTO> children;

    // --- Геттери і сеттери ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    // --- Геттери і сеттери для children ---
    public List<TestSuiteDTO> getChildren() { return children; }
    public void setChildren(List<TestSuiteDTO> children) { this.children = children; }
}



