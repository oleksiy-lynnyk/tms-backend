package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "test_run")
@Data
public class TestRun {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID projectId;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String code;

    @Column
    private String status;

    @Column
    private Instant startedAt;

    @Column
    private Instant completedAt;

    @Column
    private UUID assignedTo;
}
