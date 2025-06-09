package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.ExecutionCommandDTO;
import org.example.tmsstriker.dto.TestRunDTO;
import org.example.tmsstriker.entity.TestRun;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.repository.TestRunRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestRunService {

    private final TestRunRepository repo;
    private final CodeGeneratorService codeGeneratorService;

    public Page<TestRunDTO> getRunsByProject(UUID projectId, Pageable pageable) {
        return repo.findByProjectId(projectId, pageable).map(this::toDto);
    }

    public TestRunDTO getById(UUID id) {
        return repo.findById(id).map(this::toDto)
                .orElseThrow(() -> new ApiException("Run not found: " + id, HttpStatus.NOT_FOUND));
    }

    public TestRunDTO createRun(TestRunDTO dto) {
        TestRun entity = new TestRun();
        entity.setId(UUID.randomUUID());
        entity.setProjectId(dto.getProjectId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setAssignedTo(dto.getAssignedTo());
        entity.setStatus("DRAFT");
        entity.setStartedAt(null);
        entity.setCompletedAt(null);
        entity.setCode(codeGeneratorService.generateNextCode("test_run", dto.getProjectId(), "R-"));
        return toDto(repo.save(entity));
    }

    public TestRunDTO updateRun(UUID id, TestRunDTO dto) {
        TestRun run = repo.findById(id)
                .orElseThrow(() -> new ApiException("Run not found: " + id, HttpStatus.NOT_FOUND));
        run.setName(dto.getName());
        run.setDescription(dto.getDescription());
        run.setAssignedTo(dto.getAssignedTo());
        run.setStatus(dto.getStatus());
        return toDto(repo.save(run));
    }

    public void deleteRun(UUID id) {
        repo.deleteById(id);
    }

    public TestRunDTO executeCommand(UUID id, ExecutionCommandDTO cmd) {
        TestRun run = repo.findById(id)
                .orElseThrow(() -> new ApiException("Run not found: " + id, HttpStatus.NOT_FOUND));

        run.setStatus("RUNNING");
        run.setStartedAt(Instant.now());

        return toDto(repo.save(run));
    }

    public void completeRun(UUID id) {
        TestRun run = repo.findById(id)
                .orElseThrow(() -> new ApiException("Run not found: " + id, HttpStatus.NOT_FOUND));
        run.setStatus("COMPLETED");
        run.setCompletedAt(Instant.now());
        repo.save(run);
    }

    public TestRunDTO cloneRun(UUID id) {
        TestRun original = repo.findById(id)
                .orElseThrow(() -> new ApiException("Run not found: " + id, HttpStatus.NOT_FOUND));

        TestRun clone = new TestRun();
        clone.setId(UUID.randomUUID());
        clone.setProjectId(original.getProjectId());
        clone.setName(original.getName() + " (copy)");
        clone.setDescription(original.getDescription());
        clone.setAssignedTo(original.getAssignedTo());
        clone.setStatus("DRAFT");
        clone.setStartedAt(null);
        clone.setCompletedAt(null);
        clone.setCode(codeGeneratorService.generateNextCode("test_run", original.getProjectId(), "R-"));

        return toDto(repo.save(clone));
    }

    private TestRunDTO toDto(TestRun e) {
        TestRunDTO dto = new TestRunDTO();
        dto.setId(e.getId());
        dto.setProjectId(e.getProjectId());
        dto.setName(e.getName());
        dto.setDescription(e.getDescription());
        dto.setAssignedTo(e.getAssignedTo());
        dto.setStatus(e.getStatus());
        dto.setStartedAt(e.getStartedAt());
        dto.setCompletedAt(e.getCompletedAt());
        dto.setCode(e.getCode());
        return dto;
    }
}
