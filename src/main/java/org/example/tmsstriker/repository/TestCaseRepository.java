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

    int countByTestSuite_Id(UUID suiteId);

    // Всі запити через testSuite.project.id!
    @Query("SELECT COUNT(tc) FROM TestCase tc WHERE tc.testSuite.project.id = :projectId")
    int countByProjectId(@Param("projectId") UUID projectId);

    @Query("SELECT MAX(CAST(SUBSTRING(tc.code, 4) AS int)) FROM TestCase tc WHERE tc.testSuite.project.id = :projectId")
    Integer findMaxCodeNumberByProject(@Param("projectId") UUID projectId);

    // Якщо у тебе колись з'явиться зв’язок TestCase->Project напряму, тоді можеш так:
    // @Query("SELECT MAX(CAST(SUBSTRING(tc.code, 4) AS int)) FROM TestCase tc WHERE tc.project.id = :projectId")
    // Integer findMaxCodeNumber(@Param("projectId") UUID projectId);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(tc.code, 4) AS int)), 0) FROM TestCase tc WHERE tc.testSuite.project.id = :projectId AND tc.code LIKE 'TC-%'")
    int findMaxCodeNumberForProject(@Param("projectId") UUID projectId);

    boolean existsByTitleAndTestSuite_Id(String title, UUID suiteId);



}



