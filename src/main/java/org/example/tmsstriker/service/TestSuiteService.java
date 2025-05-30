// TestSuiteService.java
package org.example.tmsstriker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.example.tmsstriker.dto.TestSuiteDTO;
import org.example.tmsstriker.entity.TestSuite;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.repository.TestSuiteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TestSuiteService {
    private final TestSuiteRepository repo;
    private final ModelMapper mapper;

    public TestSuiteService(TestSuiteRepository repo, ModelMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    // Повернути дерево: тільки кореневі + всі діти
    public List<TestSuiteDTO> getAllSuitesAsTree() {
        List<TestSuite> all = repo.findAll();
        List<TestSuiteDTO> dtos = all.stream()
                .map(e -> mapper.map(e, TestSuiteDTO.class))
                .collect(Collectors.toList());
        // реалізація побудови дерева за parentId
        return buildTree(dtos);
    }

    // Повернути плоский список усіх сьютів
    public List<TestSuiteDTO> getAllSuites() {
        return repo.findAll().stream()
                .map(e -> mapper.map(e, TestSuiteDTO.class))
                .collect(Collectors.toList());
    }

    // Повернути один сьют за UUID
    public TestSuiteDTO getById(UUID id) {
        return repo.findById(id)
                .map(e -> mapper.map(e, TestSuiteDTO.class))
                .orElseThrow(() -> new ApiException("TestRun not found: " + id, HttpStatus.NOT_FOUND));
    }

    // Створити новий сьют
    public TestSuiteDTO createSuite(TestSuiteDTO dto) {
        TestSuite e = mapper.map(dto, TestSuite.class);
        TestSuite saved = repo.save(e);
        return mapper.map(saved, TestSuiteDTO.class);
    }

    // Оновити існуючий сьют
    public TestSuiteDTO updateSuite(UUID id, TestSuiteDTO dto) {
        TestSuite e = repo.findById(id)
                .orElseThrow(() -> new ApiException("Suite not found: " + id, HttpStatus.NOT_FOUND));
        e.setName(dto.getName());
        e.setDescription(dto.getDescription());
        e.setProjectId(dto.getProjectId());
        e.setParentId(dto.getParentId());
        TestSuite updated = repo.save(e);
        return mapper.map(updated, TestSuiteDTO.class);
    }

    // Видалити сьют
    public void deleteSuite(UUID id) {
        repo.deleteById(id);
    }

    // Допоміжний метод для дерева
    private List<TestSuiteDTO> buildTree(List<TestSuiteDTO> flat) {
        // реалізація побудови дерева
        // наприклад, групування та рекурсія
        return flat; // заглушка: повернути як є
    }

    public List<TestSuiteDTO> getSuitesTree(UUID projectId) {
        List<TestSuite> roots = repo.findByProjectIdAndParentIdIsNull(projectId);
        List<TestSuiteDTO> dtos = roots.stream()
                .map(e -> mapper.map(e, TestSuiteDTO.class))
                .collect(Collectors.toList());
        List<TestSuiteDTO> tree = buildTree(dtos); // будуєш дерево з flat-DTO

        fillTestCaseCounts(tree); // ← ВИКЛИК тут! ДО return

        return tree;
    }


    private int countTestCasesRecursive(TestSuiteDTO suite) {
        int count = suite.getTestCases() != null ? suite.getTestCases().size() : 0;
        if (suite.getChildren() != null) {
            for (TestSuiteDTO child : suite.getChildren()) {
                count += countTestCasesRecursive(child);
            }
        }
        return count;
    }

    private void fillTestCaseCounts(List<TestSuiteDTO> suites) {
        for (TestSuiteDTO suite : suites) {
            suite.setTestCaseCount(countTestCasesRecursive(suite));
            if (suite.getChildren() != null) {
                fillTestCaseCounts(suite.getChildren());
            }
        }
    }

}