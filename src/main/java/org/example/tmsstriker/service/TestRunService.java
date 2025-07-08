package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.ExecutionCommandDTO;
import org.example.tmsstriker.dto.TestRunDTO;
import org.example.tmsstriker.entity.AppUser;
import org.example.tmsstriker.entity.Configuration;
import org.example.tmsstriker.entity.Environment;
import org.example.tmsstriker.entity.Project;
import org.example.tmsstriker.entity.TestCase;
import org.example.tmsstriker.entity.TestRun;
import org.example.tmsstriker.entity.Version;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.mapper.TestRunMapper;
import org.example.tmsstriker.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestRunService {

    private final TestRunRepository testRunRepository;
    private final ProjectRepository projectRepository;
    private final AppUserRepository appUserRepository;
    private final EnvironmentRepository environmentRepository;
    private final ConfigurationRepository configurationRepository;
    private final VersionRepository versionRepository;
    private final TestCaseRepository testCaseRepository;
    private final TestRunMapper mapper;

    @Transactional(readOnly = true)
    public Page<TestRunDTO> getByProject(UUID projectId, Pageable pageable) {
        return testRunRepository.findByProject_Id(projectId, pageable)
                .map(mapper::toDto);
    }

    @Transactional
    public TestRunDTO create(TestRunDTO dto) {
        TestRun entity = new TestRun();
        fillFields(entity, dto);
        entity.setStartedAt(Instant.now());
        TestRun saved = testRunRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public TestRunDTO getById(UUID id) {
        TestRun entity = testRunRepository.findById(id)
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
        return mapper.toDto(entity);
    }

    @Transactional
    public TestRunDTO update(UUID id, TestRunDTO dto) {
        TestRun entity = testRunRepository.findById(id)
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
        fillFields(entity, dto);
        TestRun updated = testRunRepository.save(entity);
        return mapper.toDto(updated);
    }

    @Transactional
    public void delete(UUID id) {
        testRunRepository.deleteById(id);
    }

    @Transactional
    public TestRunDTO executeCommand(UUID id, ExecutionCommandDTO command) {
        TestRun entity = testRunRepository.findById(id)
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
        // Тут логіка обробки команд, наразі просто апдейтимо статус та коментар
        entity.setStatus(command.getCommand());
        entity.setDescription(command.getComment());
        TestRun updated = testRunRepository.save(entity);
        return mapper.toDto(updated);
    }

    @Transactional
    public void completeRun(UUID id) {
        TestRun entity = testRunRepository.findById(id)
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
        entity.setCompletedAt(Instant.now());
        entity.setStatus("Completed");
        testRunRepository.save(entity);
    }

    @Transactional
    public TestRunDTO cloneRun(UUID id) {
        TestRun entity = testRunRepository.findById(id)
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
        TestRun copy = new TestRun();
        copy.setProject(entity.getProject());
        copy.setCode(entity.getCode() + "-CLONE");
        copy.setName(entity.getName() + " (Clone)");
        copy.setDescription(entity.getDescription());
        copy.setStatus("Created");
        copy.setStartedAt(Instant.now());
        copy.setAssignedTo(entity.getAssignedTo());
        copy.setVersion(entity.getVersion());
        copy.setConfiguration(entity.getConfiguration());
        copy.setEnvironments(entity.getEnvironments());
        copy.setTestCases(entity.getTestCases());
        TestRun cloned = testRunRepository.save(copy);
        return mapper.toDto(cloned);
    }

    private void fillFields(TestRun entity, TestRunDTO dto) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());

        entity.setProject(projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ApiException("Project not found: " + dto.getProjectId(), HttpStatus.BAD_REQUEST)));

        if (dto.getAssignedTo() != null) {
            AppUser user = appUserRepository.findById(dto.getAssignedTo())
                    .orElseThrow(() -> new ApiException("Assigned user not found: " + dto.getAssignedTo(), HttpStatus.BAD_REQUEST));
            entity.setAssignedTo(user);
        } else {
            entity.setAssignedTo(null);
        }

        if (dto.getVersionId() != null) {
            Version version = versionRepository.findById(dto.getVersionId())
                    .orElseThrow(() -> new ApiException("Version not found: " + dto.getVersionId(), HttpStatus.BAD_REQUEST));
            entity.setVersion(version);
        } else {
            entity.setVersion(null);
        }

        if (dto.getConfigurationId() != null) {
            Configuration config = configurationRepository.findById(dto.getConfigurationId())
                    .orElseThrow(() -> new ApiException("Configuration not found: " + dto.getConfigurationId(), HttpStatus.BAD_REQUEST));
            entity.setConfiguration(config);
        } else {
            entity.setConfiguration(null);
        }

        if (dto.getEnvironmentIds() != null) {
            List<Environment> envs = environmentRepository.findAllById(dto.getEnvironmentIds());
            entity.setEnvironments(envs);
        }

        if (dto.getTestCaseIds() != null) {
            List<TestCase> cases = testCaseRepository.findAllById(dto.getTestCaseIds());
            entity.setTestCases(cases);
        }
    }
}
