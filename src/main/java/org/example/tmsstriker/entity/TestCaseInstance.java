// TestCaseInstance.java
package org.example.tmsstriker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import java.util.UUID;

@Entity
@Data
public class TestCaseInstance {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "test_case_id", columnDefinition = "uuid")
    private UUID testCaseId;

    @Column(name = "test_run_id", columnDefinition = "uuid")
    private UUID testRunId;

    private String status;
}

