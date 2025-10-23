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

    int countByTestSuite_Id(UUID suiteId);

    // ✅ ПРАВИЛЬНО - через testSuite.project.id
    @Query("SELECT COUNT(tc) FROM TestCase tc WHERE tc.testSuite.project.id = :projectId")
    int countByProjectId(@Param("projectId") UUID projectId);

    @Query("SELECT MAX(CAST(SUBSTRING(tc.code, 4) AS int)) FROM TestCase tc WHERE tc.testSuite.project.id = :projectId")
    Integer findMaxCodeNumberByProject(@Param("projectId") UUID projectId);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(tc.code, 4) AS int)), 0) FROM TestCase tc WHERE tc.testSuite.project.id = :projectId AND tc.code LIKE 'TC-%'")
    int findMaxCodeNumberForProject(@Param("projectId") UUID projectId);

    boolean existsByTitleAndTestSuite_Id(String title, UUID suiteId);

    // ✅ ВИПРАВЛЕНО - всі через testSuite.project.id
    @Query("SELECT DISTINCT tc.priority FROM TestCase tc WHERE tc.testSuite.project.id = :projectId AND tc.priority IS NOT NULL")
    List<String> findDistinctPrioritiesByProject(@Param("projectId") UUID projectId);

    @Query("SELECT DISTINCT tc.state FROM TestCase tc WHERE tc.testSuite.project.id = :projectId AND tc.state IS NOT NULL")
    List<String> findDistinctStatesByProject(@Param("projectId") UUID projectId);

    @Query("SELECT DISTINCT tc.automationStatus FROM TestCase tc WHERE tc.testSuite.project.id = :projectId AND tc.automationStatus IS NOT NULL")
    List<String> findDistinctAutomationStatusesByProject(@Param("projectId") UUID projectId);

    @Query("SELECT DISTINCT tc.owner FROM TestCase tc WHERE tc.testSuite.project.id = :projectId AND tc.owner IS NOT NULL")
    List<String> findDistinctOwnersByProject(@Param("projectId") UUID projectId);

    @Query("SELECT DISTINCT tc.component FROM TestCase tc WHERE tc.testSuite.project.id = :projectId AND tc.component IS NOT NULL")
    List<String> findDistinctComponentsByProject(@Param("projectId") UUID projectId);

    @Query("SELECT DISTINCT tc.type FROM TestCase tc WHERE tc.testSuite.project.id = :projectId AND tc.type IS NOT NULL")
    List<String> findDistinctTypesByProject(@Param("projectId") UUID projectId);

    @Query("SELECT tc.tags FROM TestCase tc WHERE tc.testSuite.project.id = :projectId AND tc.tags IS NOT NULL")
    List<String> findAllTagsByProject(@Param("projectId") UUID projectId);

    // ✅ ВИПРАВЛЕНО - статистика через testSuite.project.id
    @Query("SELECT COUNT(tc) FROM TestCase tc WHERE tc.testSuite.project.id = :projectId")
    Integer countByProject(@Param("projectId") UUID projectId);

    @Query("SELECT COUNT(tc) FROM TestCase tc WHERE tc.testSuite.project.id = :projectId AND tc.automationStatus = 'Automated'")
    Integer countAutomatedByProject(@Param("projectId") UUID projectId);

    @Query("SELECT COUNT(tc) FROM TestCase tc WHERE tc.testSuite.project.id = :projectId AND tc.automationStatus = 'Manual'")
    Integer countManualByProject(@Param("projectId") UUID projectId);
}