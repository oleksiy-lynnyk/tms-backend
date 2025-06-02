package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "project_case_sequence")
@Data
public class ProjectCaseSequence {
    @Id
    private UUID projectId;

    @Column(nullable = false)
    private Integer nextValue = 1;
}