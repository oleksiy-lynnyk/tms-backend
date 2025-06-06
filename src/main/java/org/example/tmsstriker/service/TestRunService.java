// TestRunService.java
package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.ExecutionCommandDTO;
import org.example.tmsstriker.dto.TestRunDTO;
import org.example.tmsstriker.dto.UserShortDTO;
import org.example.tmsstriker.entity.TestRun;
import org.example.tmsstriker.entity.User;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.repository.TestRunRepository;
import org.example.tmsstriker.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TestRunService {
    private final TestRunRepository repo;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public TestRunDTO getById(UUID id) {
        TestRun entity = repo.findById(id)
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
        return toDto(entity);
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
    public TestRunDTO executeCommand(UUID id, ExecutionCommandDTO cmd) {
        TestRun r = repo.findById(id)
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
        // Тут логіка твого виконання, наприклад:
        r.setStatus("RUNNING");
        // можна додати щось ще з cmd, якщо потрібно
        return toDto(repo.save(r));
    }

    @Transactional
    public void completeRun(UUID id) {
        TestRun r = repo.findById(id)
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
        r.setStatus("COMPLETED");
        r.setCompletedAt(Instant.now());
        repo.save(r);
    }

    // Клонувати запуск
    @Transactional
    public TestRunDTO cloneRun(UUID id) {
        TestRun o = repo.findById(id)
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
        TestRun c = new TestRun();
        c.setId(UUID.randomUUID());
        c.setName(o.getName() + " (copy)");
        c.setProjectId(o.getProjectId());
        c.setStatus("NEW");
        c.setStartedAt(Instant.now());
        c.setCompletedAt(null);
        c.setAssignedTo(o.getAssignedTo());
        TestRun saved = repo.save(c);
        return toDto(saved);
    }

    @Transactional
    public TestRunDTO createRun(TestRunDTO dto) {
        TestRun r = new TestRun();
        r.setProjectId(dto.getProjectId());
        r.setName(dto.getName());
        r.setDescription(dto.getDescription());
        r.setStatus("NEW");
        r.setStartedAt(Instant.now());
        // Ось тут генеруємо code:
        r.setCode(generateNextTestRunCode(dto.getProjectId()));
        if (dto.getAssignedTo() != null && dto.getAssignedTo().getId() != null) {
            userRepository.findById(dto.getAssignedTo().getId()).ifPresent(r::setAssignedTo);
        }
        TestRun saved = repo.save(r);
        return toDto(saved);
    }

    private String generateNextTestRunCode(UUID projectId) {
        long count = repo.countByProjectId(projectId);
        return "TR-" + (count + 1);
    }


    @Transactional
    public TestRunDTO updateRun(UUID id, TestRunDTO dto) {
        TestRun r = repo.findById(id)
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
        r.setName(dto.getName());
        r.setStatus(dto.getStatus());
        r.setStartedAt(dto.getStartedAt());
        r.setCompletedAt(dto.getCompletedAt());
        if (dto.getAssignedTo() != null && dto.getAssignedTo().getId() != null) {
            userRepository.findById(dto.getAssignedTo().getId()).ifPresent(r::setAssignedTo);
        } else {
            r.setAssignedTo(null);
        }
        return toDto(repo.save(r));
    }

    @Transactional
    public void deleteRun(UUID id) {
        repo.deleteById(id);
    }

    private TestRunDTO toDto(TestRun r) {
        TestRunDTO dto = new TestRunDTO();
        dto.setId(r.getId());
        dto.setProjectId(r.getProjectId());
        dto.setCode(r.getCode()); // ← ДОДАНО!
        dto.setName(r.getName());
        dto.setStatus(r.getStatus());
        dto.setStartedAt(r.getStartedAt());
        dto.setCompletedAt(r.getCompletedAt());
        if (r.getAssignedTo() != null) {
            UserShortDTO u = new UserShortDTO();
            u.setId(r.getAssignedTo().getId());
            u.setName(r.getAssignedTo().getName());
            dto.setAssignedTo(u);
        }
        return dto;
    }
}
