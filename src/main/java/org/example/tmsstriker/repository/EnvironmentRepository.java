package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.Environment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EnvironmentRepository extends JpaRepository<Environment, UUID> {
    List<Environment> findByProjectId(UUID projectId);
}
