// TestCase.java
package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Data
public class TestCase {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suite_id", columnDefinition = "uuid")
    private TestSuite testSuite;

    // Utility methods for bulk operations:
    public void copyFieldsFrom(TestCase other) {
        this.title = other.title;
        this.preconditions = other.preconditions;
        this.description = other.description;
        this.steps = other.steps;
        this.expectedResult = other.expectedResult;
        this.priority = other.priority;
        this.tags = other.tags;
        this.state = other.state;
        this.owner = other.owner;
        this.type = other.type;
        this.automationStatus = other.automationStatus;
        this.component = other.component;
        this.useCase = other.useCase;
        this.requirement = other.requirement;
    }

    public void applyFieldOperation(String fieldName, Object operationDto) {
        // Implement field-level operations here (e.g., switch on fieldName)
        switch(fieldName) {
            case "priority":
                this.priority = operationDto.toString();
                break;
            // add cases for other fields
            default:
                throw new IllegalArgumentException("Unknown field: " + fieldName);
        }
    }
}
