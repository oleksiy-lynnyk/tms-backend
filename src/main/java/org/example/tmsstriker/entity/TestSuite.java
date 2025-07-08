package org.example.tmsstriker.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "test_suite")
public class TestSuite {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private TestSuite parent;

    @Column(unique = true)
    private String code;

    // Proxy-getters/setters (НЕ ДУБЛЮВАТИ як окремі поля)
    public UUID getProjectId() {
        return project != null ? project.getId() : null;
    }
    public void setProjectId(UUID id) {
        if (this.project != null) this.project.setId(id);
        else {
            Project p = new Project();
            p.setId(id);
            this.project = p;
        }
    }

    public UUID getParentId() {
        return parent != null ? parent.getId() : null;
    }
    public void setParentId(UUID id) {
        if (this.parent != null) this.parent.setId(id);
        else if (id != null) {
            TestSuite s = new TestSuite();
            s.setId(id);
            this.parent = s;
        }
    }
}
