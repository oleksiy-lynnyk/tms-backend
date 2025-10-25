package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "test_run")
@Data
public class TestRun {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String name;

    private String code;
    private String status;
    private Instant startedAt;
    private Instant completedAt;
    private String description;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private AppUser assignedTo;

    @ManyToOne
    @JoinColumn(name = "configuration_id")
    private Configuration configuration;

    @ManyToOne
    @JoinColumn(name = "version_id")
    private Version version;

    @ManyToMany
    @JoinTable(
            name = "test_run_environments",
            joinColumns = @JoinColumn(name = "test_run_id"),
            inverseJoinColumns = @JoinColumn(name = "environment_id")
    )
    private List<Environment> environments;

    @ManyToMany
    @JoinTable(
            name = "test_run_cases",
            joinColumns = @JoinColumn(name = "test_run_id"),
            inverseJoinColumns = @JoinColumn(name = "test_case_id")
    )
    private List<TestCase> testCases;
}
