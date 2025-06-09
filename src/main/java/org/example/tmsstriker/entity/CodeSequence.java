package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "code_sequence", uniqueConstraints = @UniqueConstraint(columnNames = {"entity_type", "project_id"}))
@Data
public class CodeSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false)
    private String entityType; // 'test_run', 'test_case', 'test_suite', 'project'...

    @Column(name = "project_id")
    private UUID projectId; // для проектів може бути null

    @Column(name = "last_number", nullable = false)
    private Integer lastNumber;
}

