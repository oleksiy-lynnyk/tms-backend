package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.TestRunCaseResultAuditDTO;
import org.example.tmsstriker.entity.TestRunCaseResult;
import org.example.tmsstriker.mapper.TestRunCaseResultAuditMapper;
import org.example.tmsstriker.repository.TestRunCaseResultAuditRepository;
import org.example.tmsstriker.repository.TestRunCaseResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestRunCaseResultAuditService {
    private final TestRunCaseResultRepository resultRepo;
    private final TestRunCaseResultAuditRepository auditRepo;
    private final TestRunCaseResultAuditMapper mapper;

    @Transactional(readOnly = true)
    public List<TestRunCaseResultAuditDTO> getAuditTrail(UUID runId, UUID caseId) {
        TestRunCaseResult result = resultRepo.findByTestRun_IdAndTestCase_Id(runId, caseId)
                .orElseThrow();
        return auditRepo.findByResult_IdOrderByChangedAtAsc(result.getId())
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
