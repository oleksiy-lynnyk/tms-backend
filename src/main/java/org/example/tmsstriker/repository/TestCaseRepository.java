// TestCaseRepository.java
package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TestCaseRepository extends JpaRepository<TestCase, UUID>, JpaSpecificationExecutor<TestCase> {
    List<TestCase> findByTestSuite_Id(UUID suiteId);

    // ТІЛЬКИ ОЦЕ ДОДАЄШ ↓↓↓
    @Query("SELECT COUNT(tc) FROM TestCase tc WHERE tc.testSuite.projectId = :projectId")
    int countByProjectId(@Param("projectId") UUID projectId);

    @Query("SELECT MAX(CAST(SUBSTRING(tc.code, 4) AS int)) FROM TestCase tc WHERE tc.testSuite.projectId = :projectId")
    Integer findMaxCodeNumberByProject(@Param("projectId") UUID projectId);

}

