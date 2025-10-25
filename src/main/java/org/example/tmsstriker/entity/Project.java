package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Project {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, unique = true, length = 32)
    private String code; // PR-1, DEMO-1, AUT-15

    // Каскадне видалення всіх с’ютів при видаленні проекту
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestSuite> suites;

    // Каскадне видалення всіх тест-кейсів при видаленні проекту
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestCase> cases;

    // Каскадне видалення всіх тест-ранів при видаленні проекту
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestRun> runs;
}

