package org.example.tmsstriker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.entity.CodeSequence;
import org.example.tmsstriker.repository.CodeSequenceRepository;
import org.example.tmsstriker.repository.ProjectRepository;
import org.example.tmsstriker.repository.TestCaseRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CodeGeneratorService {

    private final CodeSequenceRepository codeSequenceRepository;
    private final TestCaseRepository testCaseRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public String generateNextCode(String entityType, UUID projectId, String prefix) {
        if (!"project".equals(entityType) && projectId == null) {
            throw new IllegalArgumentException("projectId is required for entity type: " + entityType);
        }

        UUID effectiveProjectId = "project".equals(entityType) ? null : projectId;

        System.out.println("➡️ Generating code for entity: " + entityType + ", projectId: " + effectiveProjectId);

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
            sequence = codeSequenceRepository
                    .findByEntityTypeAndProjectIdForUpdate(entityType, effectiveProjectId)
                    .orElseThrow(() -> new RuntimeException("Failed to acquire code sequence", e));
        }

        int last = sequence.getLastNumber();
        int dbMax = 0;

        if ("test_case".equals(entityType)) {
            dbMax = testCaseRepository.findMaxCodeNumberForProject(projectId);
        } else if ("project".equals(entityType)) {
            Integer maxInDb = projectRepository.findMaxProjectCodeNumber();
            dbMax = (maxInDb != null) ? maxInDb : 0;
        }

        int next = Math.max(last, dbMax) + 1;
        sequence.setLastNumber(next);

        return prefix + next;
    }
}
