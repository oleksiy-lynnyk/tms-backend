package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "test_run_case_result_audit")
@Data
public class TestRunCaseResultAudit {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "result_id")
    private TestRunCaseResult result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private AppUser changedBy;

    @Column(nullable = false)
    private String oldStatus;

    @Column(nullable = false)
    private String newStatus;

    @Column(length = 2000)
    private String comment;     // Коментар під час зміни

    private Instant changedAt;
}

