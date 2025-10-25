package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String email;

    @Column(name = "full_name")
    private String fullName;

    public String getName() {
        return fullName; // або username, якщо треба повертати username
    }
}





