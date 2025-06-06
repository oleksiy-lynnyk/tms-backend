// src/main/java/org/example/tmsstriker/entity/User.java
package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    private String name;
    private String role; // "ADMIN", "QA", "VIEWER"
}


