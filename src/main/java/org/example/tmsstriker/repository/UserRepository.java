// src/main/java/org/example/tmsstriker/repository/UserRepository.java
package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}

