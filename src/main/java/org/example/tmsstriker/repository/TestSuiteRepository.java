// TestSuiteRepository.java
package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.TestSuite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface TestSuiteRepository extends JpaRepository<TestSuite, UUID> {

    // Для повного списку (без пагінації)
    List<TestSuite> findByProjectId(UUID projectId);

    // Для пагінації (повертає Page)
    Page<TestSuite> findByProjectId(UUID projectId, Pageable pageable);

    // Для дерева, якщо використовуєш parentId:
    List<TestSuite> findByProjectIdAndParentIdIsNull(UUID projectId);
}

