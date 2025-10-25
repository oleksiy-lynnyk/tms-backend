package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Пошук по code
    Optional<Project> findByCode(String code);

    // Додаємо ось так:
    @Query("SELECT MAX(CAST(SUBSTRING(p.code, 4) AS int)) FROM Project p WHERE p.code LIKE 'PR-%'")
    Integer findMaxProjectCodeNumber();

    boolean existsByName(String name);

}


