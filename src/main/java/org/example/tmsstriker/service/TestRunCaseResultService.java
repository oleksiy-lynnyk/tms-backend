package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.TestRunCaseResultDTO;
import org.example.tmsstriker.entity.*;
import org.example.tmsstriker.mapper.TestRunCaseResultMapper;
import org.example.tmsstriker.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.example.tmsstriker.exception.ApiException;
import org.springframework.http.HttpStatus;


import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestRunCaseResultService {
    private final TestRunCaseResultRepository repo;
    private final TestRunRepository testRunRepo;
    private final TestCaseRepository testCaseRepo;
    private final AppUserRepository appUserRepo;
    private final TestRunCaseResultMapper mapper;
    private final TestRunCaseResultAuditRepository auditRepo;

// Замінити createOrUpdateResult метод:

    @Transactional
    public TestRunCaseResultDTO createOrUpdateResult(UUID runId, UUID caseId, TestRunCaseResultDTO dto) {
        TestRunCaseResult result = repo.findByTestRun_IdAndTestCase_Id(runId, caseId)
                .orElseGet(() -> {
                    TestRunCaseResult newResult = new TestRunCaseResult();

                    // ВИПРАВЛЕНО: використовуємо ApiException замість orElseThrow()
                    TestRun testRun = testRunRepo.findById(runId)
                            .orElseThrow(() -> new ApiException("TestRun not found: " + runId, HttpStatus.NOT_FOUND));
                    TestCase testCase = testCaseRepo.findById(caseId)
                            .orElseThrow(() -> new ApiException("TestCase not found: " + caseId, HttpStatus.NOT_FOUND));

                    newResult.setTestRun(testRun);
                    newResult.setTestCase(testCase);
                    return newResult;
                });

        String oldStatus = result.getStatus();
        String newStatus = dto.getStatus();

        result.setExecutedBy(dto.getExecutedBy() != null ? appUserRepo.findById(dto.getExecutedBy()).orElse(null) : null);
        result.setStatus(newStatus);
        result.setComment(dto.getComment());
        result.setExecutedAt(Instant.now());

        TestRunCaseResult saved = repo.save(result);

        if (oldStatus == null || !oldStatus.equals(newStatus)) {
            TestRunCaseResultAudit audit = new TestRunCaseResultAudit();
            audit.setResult(saved);
            audit.setChangedBy(saved.getExecutedBy());
            audit.setOldStatus(oldStatus != null ? oldStatus : "Untested");
            audit.setNewStatus(newStatus);
            audit.setComment(dto.getComment());
            audit.setChangedAt(Instant.now());
            auditRepo.save(audit);
        }

        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<TestRunCaseResultDTO> getResultsForRun(UUID runId) {
        return repo.findByTestRun_Id(runId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TestRunCaseResultDTO getResult(UUID runId, UUID caseId) {
        TestRunCaseResult entity = repo.findByTestRun_IdAndTestCase_Id(runId, caseId)
                .orElseThrow(() -> new ApiException("Result not found", HttpStatus.NOT_FOUND));
        return mapper.toDto(entity);
    }
}
