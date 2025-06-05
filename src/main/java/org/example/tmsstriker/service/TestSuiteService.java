// TestSuiteService.java
package org.example.tmsstriker.service;

import java.util.*;
import java.util.stream.Collectors;

import org.example.tmsstriker.dto.TestSuiteDTO;
import org.example.tmsstriker.entity.TestSuite;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.repository.TestCaseRepository;
import org.example.tmsstriker.repository.TestSuiteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TestSuiteService {
    private final TestSuiteRepository repo;
    private final TestCaseRepository testCaseRepo;
    private final ModelMapper mapper;

    public TestSuiteService(TestSuiteRepository repo, TestCaseRepository testCaseRepo, ModelMapper mapper) {
        this.repo = repo;
        this.testCaseRepo = testCaseRepo;
        this.mapper = mapper;
    }

    /** Плоский список усіх сьютів */
    public List<TestSuiteDTO> getAllSuites() {
        return repo.findAll().stream()
                .map(e -> mapper.map(e, TestSuiteDTO.class))
                .collect(Collectors.toList());
    }

    /** Один сьют за UUID */
    public TestSuiteDTO getById(UUID id) {
        return repo.findById(id)
                .map(e -> mapper.map(e, TestSuiteDTO.class))
                .orElseThrow(() -> new ApiException("Suite not found: " + id, HttpStatus.NOT_FOUND));
    }

    /** Створити новий сьют */
    public TestSuiteDTO createSuite(TestSuiteDTO dto) {
        TestSuite e = mapper.map(dto, TestSuite.class);
        TestSuite saved = repo.save(e);
        return mapper.map(saved, TestSuiteDTO.class);
    }

    /** Оновити існуючий сьют */
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

    /** Видалити сьют */
    public void deleteSuite(UUID id) {
        repo.deleteById(id);
    }

    /**
     * ДЕРЕВО сьютів по проекту (корені + всі нащадки)
     */
    public List<TestSuiteDTO> getSuitesTree(UUID projectId) {
        // Плоский список усіх сьютів проєкту:
        List<TestSuite> flat = repo.findByProjectId(projectId);

        // Мапимо в DTO:
        List<TestSuiteDTO> flatDtos = flat.stream()
                .map(e -> mapper.map(e, TestSuiteDTO.class))
                .collect(Collectors.toList());

        // Будуємо дерево:
        List<TestSuiteDTO> tree = buildTree(flatDtos);

        // Підрахунок кейсів:
        fillTestCaseCounts(tree);

        return tree;
    }

    /** Побудова дерева з плоского списку */
    private List<TestSuiteDTO> buildTree(List<TestSuiteDTO> flat) {
        Map<UUID, TestSuiteDTO> byId = new HashMap<>();
        List<TestSuiteDTO> roots = new ArrayList<>();

        for (TestSuiteDTO suite : flat) {
            byId.put(suite.getId(), suite);
            suite.setChildren(new ArrayList<>()); // children завжди не null
        }

        for (TestSuiteDTO suite : flat) {
            UUID parentId = suite.getParentId();
            if (parentId != null && byId.containsKey(parentId)) {
                byId.get(parentId).getChildren().add(suite);
            } else {
                roots.add(suite);
            }
        }
        return roots;
    }

    /** Рекурсивно рахує testCaseCount для всіх сьютів (children не null!) */
    private void fillTestCaseCounts(List<TestSuiteDTO> suites) {
        if (suites == null) return;
        for (TestSuiteDTO suite : suites) {
            // 1. Кількість кейсів напряму в цьому сьюті (з БД)
            int count = testCaseRepo.countByTestSuite_Id(suite.getId());

            // 2. Рекурсивно додаємо кількість кейсів у дочірніх сьютах
            if (suite.getChildren() != null && !suite.getChildren().isEmpty()) {
                fillTestCaseCounts(suite.getChildren());
                for (TestSuiteDTO child : suite.getChildren()) {
                    count += child.getTestCaseCount();
                }
            }

            // 3. Встановлюємо фінальне значення
            suite.setTestCaseCount(count);
        }
    }


    /** Опціонально: дерево усіх сьютів */
    public List<TestSuiteDTO> getAllSuitesAsTree() {
        List<TestSuite> all = repo.findAll();
        List<TestSuiteDTO> dtos = all.stream()
                .map(e -> mapper.map(e, TestSuiteDTO.class))
                .collect(Collectors.toList());
        return buildTree(dtos);
    }
}

