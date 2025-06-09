// src/main/java/org/example/tmsstriker/repository/ConfigurationRepository.java
package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConfigurationRepository extends JpaRepository<Configuration, UUID> {
    List<Configuration> findByProjectId(UUID projectId);
}

