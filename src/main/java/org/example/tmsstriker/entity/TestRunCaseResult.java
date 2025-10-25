package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "test_run_case_results",
        uniqueConstraints = @UniqueConstraint(columnNames = {"test_run_id", "test_case_id"}))
@Data
public class TestRunCaseResult {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_run_id")
    private TestRun testRun;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_case_id")
    private TestCase testCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executed_by")
    private AppUser executedBy;

    @Column(nullable = false)
    private String status; // Passed, Failed, Blocked, Skipped, Untested, Retest...

    @Column(length = 2000)
    private String comment;

    private Instant executedAt;
}

