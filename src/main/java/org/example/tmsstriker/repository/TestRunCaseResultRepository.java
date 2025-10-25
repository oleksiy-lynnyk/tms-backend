package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.TestRunCaseResult;
import org.example.tmsstriker.entity.TestRun;
import org.example.tmsstriker.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestRunCaseResultRepository extends JpaRepository<TestRunCaseResult, UUID> {
    List<TestRunCaseResult> findByTestRun_Id(UUID testRunId);
    List<TestRunCaseResult> findByTestRun(TestRun run);
    List<TestRunCaseResult> findByTestCase_Id(UUID testCaseId);
    Optional<TestRunCaseResult> findByTestRun_IdAndTestCase_Id(UUID testRunId, UUID testCaseId);
}
