// ProjectRepository.java
package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Пошук по code
    Optional<Project> findByCode(String code);
}

