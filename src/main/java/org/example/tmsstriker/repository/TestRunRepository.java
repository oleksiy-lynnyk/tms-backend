// TestRunRepository.java
package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.Project;
import org.example.tmsstriker.entity.TestRun;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TestRunRepository extends JpaRepository<TestRun, UUID> {
    Page<TestRun> findByProject_Id(UUID projectId, Pageable pageable);

    long countByProject(Project project);
}

