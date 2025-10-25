package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.TestSuiteDTO;
import org.example.tmsstriker.entity.Project;
import org.example.tmsstriker.entity.TestSuite;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.repository.ProjectRepository;
import org.example.tmsstriker.repository.TestSuiteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TestSuiteService {
    private final TestSuiteRepository repo;
    private final ProjectRepository projectRepository;
    private final CodeGeneratorService codeGeneratorService;

    // --- CRUD ---

    public TestSuiteDTO getById(UUID id) {
        return repo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ApiException("TestSuite not found: " + id, HttpStatus.NOT_FOUND));
    }

    public List<TestSuiteDTO> getSuitesByProject(UUID projectId) {
        return repo.findByProject_Id(projectId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Page<TestSuiteDTO> getSuitesPage(UUID projectId, Pageable pageable) {
        return repo.findByProject_Id(projectId, pageable)
                .map(this::toDto);
    }

    @Transactional
    public TestSuiteDTO createSuite(TestSuiteDTO dto) {
        if (dto.getProjectId() == null) {
            throw new ApiException("Project ID is required to create a Test Suite", HttpStatus.BAD_REQUEST);
        }

        // перевірка дубліката
        if (repo.existsByNameAndProject_Id(dto.getName(), dto.getProjectId())) {
            throw new ApiException(
                    "Duplicate Test Suite name: '" + dto.getName() + "' in project " + dto.getProjectId(),
                    HttpStatus.CONFLICT
            );
        }

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ApiException("Project not found: " + dto.getProjectId(), HttpStatus.NOT_FOUND));

        TestSuite suite = new TestSuite();
        suite.setId(UUID.randomUUID());
        suite.setProject(project);
        suite.setName(dto.getName());
        suite.setDescription(dto.getDescription());

        if (dto.getParentId() != null) {
            TestSuite parent = repo.findById(dto.getParentId())
                    .orElseThrow(() -> new ApiException("Parent suite not found: " + dto.getParentId(), HttpStatus.BAD_REQUEST));
            suite.setParent(parent);
        }

        if (dto.getCode() == null || dto.getCode().isEmpty()) {
            suite.setCode(codeGeneratorService.generateNextCode("test_suite", project.getId(), "TS-"));
        } else {
            suite.setCode(dto.getCode());
        }

        return toDto(repo.save(suite));
    }

    @Transactional
    public TestSuiteDTO updateSuite(UUID id, TestSuiteDTO dto) {
        TestSuite suite = repo.findById(id)
                .orElseThrow(() -> new ApiException("TestSuite not found: " + id, HttpStatus.NOT_FOUND));

        suite.setName(dto.getName());
        suite.setDescription(dto.getDescription());

        if (dto.getCode() != null) {
            suite.setCode(dto.getCode());
        }

        return toDto(repo.save(suite));
    }

    @Transactional
    public void deleteSuite(UUID id) {
        repo.deleteById(id);
    }

    // --- TREE / FLAT ---

    public List<TestSuiteDTO> getSuitesTree(UUID projectId) {
        if (projectId == null) {
            throw new ApiException("Missing projectId", HttpStatus.BAD_REQUEST);
        }

        List<TestSuite> all = repo.findByProject_Id(projectId);
        List<TestSuite> roots = all.stream()
                .filter(s -> s.getParent() == null)
                .collect(Collectors.toList());

        return roots.stream().map(r -> toDtoWithChildren(r, all)).collect(Collectors.toList());
    }

    public List<TestSuiteDTO> getAllSuitesAsTree() {
        List<TestSuite> all = repo.findAll();
        List<TestSuite> roots = all.stream()
                .filter(s -> s.getParent() == null)
                .collect(Collectors.toList());

        return roots.stream().map(r -> toDtoWithChildren(r, all)).collect(Collectors.toList());
    }

    public List<TestSuiteDTO> getAllSuites() {
        return repo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // --- Mappers ---

    private TestSuiteDTO toDto(TestSuite s) {
        TestSuiteDTO dto = new TestSuiteDTO();
        dto.setId(s.getId());
        dto.setProjectId(s.getProject() != null ? s.getProject().getId() : null);
        dto.setParentId(s.getParent() != null ? s.getParent().getId() : null);
        dto.setName(s.getName());
        dto.setDescription(s.getDescription());
        dto.setCode(s.getCode());
        return dto;
    }

    private TestSuiteDTO toDtoWithChildren(TestSuite suite, List<TestSuite> all) {
        TestSuiteDTO dto = toDto(suite);
        List<TestSuiteDTO> children = all.stream()
                .filter(s -> suite.getId().equals(s.getParent() != null ? s.getParent().getId() : null))
                .map(s -> toDtoWithChildren(s, all))
                .collect(Collectors.toList());
        dto.setChildren(children);
        return dto;
    }
}
