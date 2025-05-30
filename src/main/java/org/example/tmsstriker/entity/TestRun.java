// TestRun.java
package org.example.tmsstriker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import java.util.UUID;
import java.time.Instant;

@Entity
@Data
public class TestRun {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "project_id", columnDefinition = "uuid")
    private UUID projectId;

    private String name;
    private String status;
    private Instant startedAt;
    private Instant finishedAt;
}