package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.TestRunCaseResultAudit;
import org.example.tmsstriker.entity.TestRunCaseResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TestRunCaseResultAuditRepository extends JpaRepository<TestRunCaseResultAudit, UUID> {
    List<TestRunCaseResultAudit> findByResult_IdOrderByChangedAtAsc(UUID resultId);
    List<TestRunCaseResultAudit> findByResult(TestRunCaseResult result);
}
