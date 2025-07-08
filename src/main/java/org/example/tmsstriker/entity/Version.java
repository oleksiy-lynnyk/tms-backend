package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "version")
@Data
public class Version {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String name;

    @Column(length = 50)
    private String slug;

    @Column
    private String description;

    // Якщо треба доступ до projectId окремо:
    public UUID getProjectId() {
        return project != null ? project.getId() : null;
    }
    public void setProjectId(UUID id) {
        if (this.project != null) this.project.setId(id);
        else if (id != null) {
            Project p = new Project();
            p.setId(id);
            this.project = p;
        }
    }
}
