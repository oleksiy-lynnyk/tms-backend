// src/main/java/org/example/tmsstriker/repository/AppUserRepository.java
package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmail(String email);
}

