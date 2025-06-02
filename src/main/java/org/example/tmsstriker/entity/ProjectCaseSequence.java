package org.example.tmsstriker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "project_case_sequence")
public class ProjectCaseSequence {
    @Id
    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "next_value", nullable = false)
    private int nextValue;

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public int getNextValue() { return nextValue; }
    public void setNextValue(int nextValue) { this.nextValue = nextValue; }
}

