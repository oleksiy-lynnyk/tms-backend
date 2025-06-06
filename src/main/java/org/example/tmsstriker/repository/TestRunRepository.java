// TestRunRepository.java
package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.TestRun;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TestRunRepository extends JpaRepository<TestRun, UUID> {

    Page<TestRun> findByProjectId(UUID projectId, Pageable pageable);

    long countByProjectId(UUID projectId);

}
