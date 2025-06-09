// src/main/java/org/example/tmsstriker/entity/Configuration.java
package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Configuration {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID projectId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 100)
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

