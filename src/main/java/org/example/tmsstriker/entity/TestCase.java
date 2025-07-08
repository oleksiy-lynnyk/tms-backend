package org.example.tmsstriker.entity;

import lombok.Data;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "test_case")
public class TestCase {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String title;

    @Column
    private String preconditions;

    @Column
    private String description;

    @OneToMany(mappedBy = "testCase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestStep> steps;

    @Column
    private String priority;
    @Column
    private String state;
    @Column
    private String type;
    @Column
    private String component;
    @Column
    private String automationStatus;
    @Column
    private String requirement;
    @Column
    private String owner;
    @Column
    private String tags;

    @ManyToOne
    @JoinColumn(name = "suite_id")
    private TestSuite testSuite;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column
    private String useCase;

    // Якщо вам потрібні геттери/сеттери suiteId окремо:
    public UUID getSuiteId() {
        return testSuite != null ? testSuite.getId() : null;
    }

    public void setSuiteId(UUID suiteId) {
        if (suiteId == null) {
            this.testSuite = null;
        } else {
            TestSuite suite = new TestSuite();
            suite.setId(suiteId);
            this.testSuite = suite;
        }
    }

    public void addStep(TestStep step) {
        if (steps == null) steps = new java.util.ArrayList<>();
        steps.add(step);
        step.setTestCase(this);
    }
}
