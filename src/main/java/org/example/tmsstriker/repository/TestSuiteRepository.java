package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.TestSuite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface TestSuiteRepository extends JpaRepository<TestSuite, UUID> {

    // Для повного списку (без пагінації)
    List<TestSuite> findByProject_Id(UUID projectId);

    // Для пагінації (повертає Page)
    Page<TestSuite> findByProject_Id(UUID projectId, Pageable pageable);

    // Для дерева, якщо використовуєш parent (root-сьюти)
    List<TestSuite> findByProject_IdAndParentIsNull(UUID projectId);

    boolean existsByNameAndProject_Id(String name, UUID projectId);

}
