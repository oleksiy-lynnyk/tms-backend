// TestSuiteRepository.java
package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.TestSuite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface TestSuiteRepository extends JpaRepository<TestSuite, UUID> {

    // Додай цей метод, якщо його ще нема!
    List<TestSuite> findByProjectId(UUID projectId);

    // Для дерева, якщо використовуєш parentId:
    List<TestSuite> findByProjectIdAndParentIdIsNull(UUID projectId);
}
