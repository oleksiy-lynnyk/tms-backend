// TestRunService.java
package org.example.tmsstriker.service;
import org.springframework.http.HttpStatus;

import org.example.tmsstriker.dto.ExecutionCommandDTO;
import org.example.tmsstriker.dto.TestRunDTO;
import org.example.tmsstriker.entity.TestRun;
import org.example.tmsstriker.repository.TestRunRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.tmsstriker.exception.ApiException;


import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class TestRunService {
    private final TestRunRepository repo;
    private final ModelMapper mapper;


    public TestRunService(TestRunRepository repo, ModelMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    /**
     * Отримати один запуск за UUID
     */
    public TestRunDTO getById(UUID id) {
        TestRun entity = repo.findById(id)
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
        return mapper.map(entity, TestRunDTO.class);
    }


    public Page<TestRunDTO> getRunsByProject(UUID projectId, Pageable pageable) {
        return repo.findByProjectId(projectId, pageable)
                .map(this::toDto);
    }

    public TestRunDTO getRunById(UUID id) {
        return repo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public TestRunDTO createRun(TestRunDTO dto) {
        TestRun r = new TestRun();
        r.setProjectId(dto.getProjectId());
        r.setName(dto.getName());
        r.setStatus("NEW");
        r.setStartedAt(Instant.now());
        TestRun saved = repo.save(r);
        return toDto(saved);
    }

    @Transactional
    public TestRunDTO updateRun(UUID id, TestRunDTO dto) {
        TestRun r = repo.findById(id)
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
        r.setName(dto.getName());
        r.setStatus(dto.getStatus());
        r.setStartedAt(dto.getStartedAt());
        r.setFinishedAt(dto.getCompletedAt());
        return toDto(repo.save(r));
    }

    @Transactional
    public void deleteRun(UUID id) {
        repo.deleteById(id);
    }

    @Transactional
    public TestRunDTO executeCommand(UUID id, ExecutionCommandDTO cmd) {
        TestRun r = repo.findById(id)
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
        r.setStatus("RUNNING");
        return toDto(repo.save(r));
    }

    @Transactional
    public void completeRun(UUID id) {
        TestRun r = repo.findById(id)
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
        r.setStatus("COMPLETED");
        r.setFinishedAt(Instant.now());
        repo.save(r);
    }

    @Transactional
    public TestRunDTO cloneRun(UUID id) {
        TestRun o = repo.findById(id)
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
        TestRun c = new TestRun();
        c.setName(o.getName() + " (copy)");
        c.setProjectId(o.getProjectId());
        c.setStatus("NEW");
        TestRun saved = repo.save(c);
        return toDto(saved);
    }

    private TestRunDTO toDto(TestRun r) {
        TestRunDTO dto = new TestRunDTO();
        dto.setId(r.getId());
        dto.setProjectId(r.getProjectId());
        dto.setName(r.getName());
        dto.setStatus(r.getStatus());
        dto.setStartedAt(r.getStartedAt());
        dto.setCompletedAt(r.getFinishedAt());
        return dto;
    }
}