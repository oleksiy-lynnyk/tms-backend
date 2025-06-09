package org.example.tmsstriker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.entity.CodeSequence;
import org.example.tmsstriker.repository.CodeSequenceRepository;
import org.example.tmsstriker.repository.TestCaseRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CodeGeneratorService {

    private final CodeSequenceRepository codeSequenceRepository;
    private final TestCaseRepository testCaseRepository;

    @Transactional
    public String generateNextCode(String entityType, UUID projectId, String prefix) {
        // 🔐 Для "project" — немає projectId
        UUID effectiveProjectId = "project".equals(entityType) ? null : projectId;
        CodeSequence sequence;

        try {
            sequence = codeSequenceRepository
                    .findByEntityTypeAndProjectIdForUpdate(entityType, effectiveProjectId)
                    .orElseGet(() -> {
                        CodeSequence newSeq = new CodeSequence();
                        newSeq.setEntityType(entityType);
                        newSeq.setProjectId(effectiveProjectId);
                        newSeq.setLastNumber(0);
                        return codeSequenceRepository.save(newSeq);
                    });
        } catch (Exception e) {
            // Якщо гонка — повторити
            sequence = codeSequenceRepository
                    .findByEntityTypeAndProjectIdForUpdate(entityType, effectiveProjectId)
                    .orElseThrow();
        }

        int last = sequence.getLastNumber();
        int dbMax = 0;

        if ("test_case".equals(entityType)) {
            dbMax = testCaseRepository.findMaxCodeNumberForProject(projectId);
        }

        int next = Math.max(last, dbMax) + 1;
        sequence.setLastNumber(next);

        return prefix + next;
    }
}
