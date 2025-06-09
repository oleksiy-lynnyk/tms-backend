package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.TestSuiteDTO;
import org.example.tmsstriker.entity.TestSuite;
import org.example.tmsstriker.exception.ApiException;
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
    private final CodeGeneratorService codeGeneratorService;

    // --- CRUD ---
    public TestSuiteDTO getById(UUID id) {
        return repo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ApiException("TestSuite not found: " + id, HttpStatus.NOT_FOUND));
    }

    public List<TestSuiteDTO> getSuitesByProject(UUID projectId) {
        return repo.findByProjectId(projectId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Page<TestSuiteDTO> getSuitesPage(UUID projectId, Pageable pageable) {
        return repo.findByProjectId(projectId, pageable)
                .map(this::toDto);
    }

    @Transactional
    public TestSuiteDTO createSuite(TestSuiteDTO dto) {
        TestSuite suite = toEntity(dto);
        if (suite.getCode() == null || suite.getCode().isEmpty()) {
            String code = codeGeneratorService.generateNextCode("test_suite", suite.getProjectId(), "TS-");
            suite.setCode(code);
        }
        TestSuite saved = repo.save(suite);
        return toDto(saved);
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

    // --- TREE / FLAT LOGIC ---

    /** Дерево сьютів для одного проекту */
    public List<TestSuiteDTO> getSuitesTree(UUID projectId) {
        List<TestSuite> all = repo.findByProjectId(projectId);
        // шукаємо тільки root-елементи (parentId == null)
        List<TestSuite> roots = all.stream()
                .filter(s -> s.getParentId() == null)
                .collect(Collectors.toList());
        return roots.stream().map(r -> toDtoWithChildren(r, all)).collect(Collectors.toList());
    }

    /** Дерево всіх сьютів (по всіх проектах) */
    public List<TestSuiteDTO> getAllSuitesAsTree() {
        List<TestSuite> all = repo.findAll();
        List<TestSuite> roots = all.stream()
                .filter(s -> s.getParentId() == null)
                .collect(Collectors.toList());
        return roots.stream().map(r -> toDtoWithChildren(r, all)).collect(Collectors.toList());
    }

    /** Плоский список всіх сьютів */
    public List<TestSuiteDTO> getAllSuites() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    // --- Маппери ---
    private TestSuiteDTO toDtoWithChildren(TestSuite suite, List<TestSuite> all) {
        TestSuiteDTO dto = toDto(suite);
        List<TestSuiteDTO> children = all.stream()
                .filter(s -> suite.getId().equals(s.getParentId()))
                .map(s -> toDtoWithChildren(s, all))
                .collect(Collectors.toList());
        dto.setChildren(children);
        return dto;
    }

    private TestSuiteDTO toDto(TestSuite s) {
        TestSuiteDTO dto = new TestSuiteDTO();
        dto.setId(s.getId());
        dto.setProjectId(s.getProjectId());
        dto.setParentId(s.getParentId());
        dto.setName(s.getName());
        dto.setDescription(s.getDescription());
        dto.setCode(s.getCode());
        // Дітей НЕ додаємо тут, тільки в toDtoWithChildren
        return dto;
    }

    private TestSuite toEntity(TestSuiteDTO dto) {
        TestSuite s = new TestSuite();
        if (dto.getId() != null) s.setId(dto.getId());
        s.setProjectId(dto.getProjectId());
        s.setParentId(dto.getParentId());
        s.setName(dto.getName());
        s.setDescription(dto.getDescription());
        s.setCode(dto.getCode());
        return s;
    }
}
