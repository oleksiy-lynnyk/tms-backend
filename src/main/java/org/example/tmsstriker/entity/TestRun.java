// TestRun.java
package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

// src/main/java/org/example/tmsstriker/entity/TestRun.java

@Entity
@Table(name = "test_runs")
@Data
public class TestRun {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID projectId;

    @Column(unique = true, nullable = false)
    private String code; // ← ДОДАНО!

    private String name;
    private String description;
    private String status;
    private Instant startedAt;
    private Instant completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;
}
