package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class TestCase {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 32)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suite_id", columnDefinition = "uuid")
    private TestSuite testSuite;

    @Column(name = "project_id", columnDefinition = "uuid", nullable = false)
    private UUID projectId;

    // Додаємо кроки
    @OneToMany(mappedBy = "testCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    private List<TestStep> steps = new ArrayList<>();

    public void setTestSuite(TestSuite suite) {
        this.testSuite = suite;
        if (suite != null) {
            this.projectId = suite.getProjectId();
        } else {
            this.projectId = null;
        }
    }

    // Utility methods for bulk operations:
    public void copyFieldsFrom(TestCase other) {
        this.title = other.title;
        this.preconditions = other.preconditions;
        this.description = other.description;
        this.priority = other.priority;
        this.tags = other.tags;
        this.state = other.state;
        this.owner = other.owner;
        this.type = other.type;
        this.automationStatus = other.automationStatus;
        this.component = other.component;
        this.useCase = other.useCase;
        this.requirement = other.requirement;
        this.projectId = other.projectId;
        // НЕ копіюємо steps тут, це окремо!
    }

    public void applyFieldOperation(String fieldName, Object operationDto) {
        switch(fieldName) {
            case "priority":
                this.priority = operationDto.toString();
                break;
            default:
                throw new IllegalArgumentException("Unknown field: " + fieldName);
        }
    }
}
