package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "configuration")
@Data
public class Configuration {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project; // <-- зв'язок із Project

    @Column(nullable = false)
    private String name;

    @Column(length = 100)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String os;

    @Column(length = 50)
    private String browser;

    @Column(length = 50)
    private String device;
}
