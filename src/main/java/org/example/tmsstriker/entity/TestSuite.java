package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.util.List;

@Entity
@Data
public class TestSuite {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    private String name;
    private String description;

    // projectId — просте поле UUID (НЕ ManyToOne)
    @Column(name = "project_id", columnDefinition = "uuid", nullable = false)
    private UUID projectId;

    @Column(name = "parent_id", columnDefinition = "uuid")
    private UUID parentId;

    @Transient
    private List<TestSuite> children;
}
