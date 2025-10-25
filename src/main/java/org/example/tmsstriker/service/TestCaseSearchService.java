package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tmsstriker.dto.TestCaseDTO;
import org.example.tmsstriker.dto.TestCaseFiltersDTO;
import org.example.tmsstriker.dto.TestCaseSearchDTO;
import org.example.tmsstriker.dto.TestCaseStatsDTO;
import org.example.tmsstriker.entity.TestCase;
import org.example.tmsstriker.repository.TestCaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestCaseSearchService {

    private final TestCaseRepository testCaseRepository;

    public Page<TestCaseDTO> searchTestCases(TestCaseSearchDTO searchParams, Pageable pageable) {
        log.debug("Searching test cases with params: {}", searchParams);

        Specification<TestCase> spec = buildSearchSpecification(searchParams);
        Page<TestCase> testCases = testCaseRepository.findAll(spec, pageable);

        return testCases.map(this::mapToDTO);
    }

    public TestCaseFiltersDTO getFilterValues(UUID projectId) {
        log.debug("Getting filter values for projectId: {}", projectId);

        return TestCaseFiltersDTO.builder()
                .priorities(getDistinctValues(projectId, "priority"))
                .statuses(getDistinctValues(projectId, "state"))
                .automationStatuses(getDistinctValues(projectId, "automationStatus"))
                .owners(getDistinctValues(projectId, "owner"))
                .components(getDistinctValues(projectId, "component"))
                .types(getDistinctValues(projectId, "type"))
                .tags(getDistinctTags(projectId))
                .stats(buildStats(projectId))
                .build();
    }

    private Specification<TestCase> buildSearchSpecification(TestCaseSearchDTO params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Project filter - ВИПРАВЛЕНО!
            if (params.getProjectId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("testSuite").get("project").get("id"), params.getProjectId()));
            }

            // Suite filter - ВИПРАВЛЕНО!
            if (params.getSuiteId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("testSuite").get("id"), params.getSuiteId()));
            }

            // Text search in title and description
            if (params.getQuery() != null && !params.getQuery().trim().isEmpty()) {
                String searchTerm = "%" + params.getQuery().toLowerCase() + "%";
                Predicate titleMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")), searchTerm);
                Predicate descMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")), searchTerm);
                predicates.add(criteriaBuilder.or(titleMatch, descMatch));
            }

            // Priority filter
            if (params.getPriority() != null && !params.getPriority().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), params.getPriority()));
            }

            // Status filter
            if (params.getStatus() != null && !params.getStatus().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("state"), params.getStatus()));
            }

            // Automation status filter
            if (params.getAutomationStatus() != null && !params.getAutomationStatus().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("automationStatus"), params.getAutomationStatus()));
            }

            // Owner filter
            if (params.getOwner() != null && !params.getOwner().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("owner"), params.getOwner()));
            }

            // Component filter
            if (params.getComponent() != null && !params.getComponent().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("component"), params.getComponent()));
            }

            // Type filter
            if (params.getType() != null && !params.getType().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("type"), params.getType()));
            }

            // Tags filter (comma-separated)
            if (params.getTags() != null && !params.getTags().trim().isEmpty()) {
                List<String> tagList = Arrays.asList(params.getTags().split(","))
                        .stream()
                        .map(String::trim)
                        .filter(tag -> !tag.isEmpty())
                        .collect(Collectors.toList());

                if (!tagList.isEmpty()) {
                    List<Predicate> tagPredicates = tagList.stream()
                            .map(tag -> criteriaBuilder.like(
                                    criteriaBuilder.lower(root.get("tags")),
                                    "%" + tag.toLowerCase() + "%"))
                            .collect(Collectors.toList());

                    predicates.add(criteriaBuilder.or(
                            tagPredicates.toArray(new Predicate[0])));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<String> getDistinctValues(UUID projectId, String fieldName) {
        if (projectId == null) {
            return List.of(); // Якщо проект не вказано, повертаємо порожній список
        }

        return switch (fieldName) {
            case "priority" -> testCaseRepository.findDistinctPrioritiesByProject(projectId);
            case "state" -> testCaseRepository.findDistinctStatesByProject(projectId);
            case "automationStatus" -> testCaseRepository.findDistinctAutomationStatusesByProject(projectId);
            case "owner" -> testCaseRepository.findDistinctOwnersByProject(projectId);
            case "component" -> testCaseRepository.findDistinctComponentsByProject(projectId);
            case "type" -> testCaseRepository.findDistinctTypesByProject(projectId);
            default -> List.of();
        };
    }

    private List<String> getDistinctTags(UUID projectId) {
        if (projectId == null) {
            return List.of();
        }

        List<String> allTags = testCaseRepository.findAllTagsByProject(projectId);

        // Розбиваємо теги через кому та створюємо унікальний список
        return allTags.stream()
                .filter(tags -> tags != null && !tags.trim().isEmpty())
                .flatMap(tags -> Arrays.stream(tags.split(",")))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private TestCaseStatsDTO buildStats(UUID projectId) {
        if (projectId == null) {
            return TestCaseStatsDTO.builder()
                    .totalTestCases(0)
                    .automatedCount(0)
                    .manualCount(0)
                    .automationCoverage(0.0)
                    .build();
        }

        Integer total = testCaseRepository.countByProject(projectId);
        Integer automated = testCaseRepository.countAutomatedByProject(projectId);
        Integer manual = testCaseRepository.countManualByProject(projectId);

        return TestCaseStatsDTO.builder()
                .totalTestCases(total)
                .automatedCount(automated)
                .manualCount(manual)
                .automationCoverage(total > 0 ? (automated * 100.0) / total : 0.0)
                .build();
    }

    private TestCaseDTO mapToDTO(TestCase testCase) {
        TestCaseDTO dto = new TestCaseDTO();
        dto.setId(testCase.getId());
        dto.setCode(testCase.getCode());
        dto.setTitle(testCase.getTitle());
        dto.setDescription(testCase.getDescription());
        dto.setPriority(testCase.getPriority());
        dto.setState(testCase.getState());
        dto.setType(testCase.getType());
        dto.setComponent(testCase.getComponent());
        dto.setAutomationStatus(testCase.getAutomationStatus());
        dto.setOwner(testCase.getOwner());
        dto.setTags(testCase.getTags());
        dto.setPreconditions(testCase.getPreconditions());
        dto.setRequirement(testCase.getRequirement());
        dto.setUseCase(testCase.getUseCase());

        // ВИПРАВЛЕНО - використовуємо правильні поля!
        if (testCase.getTestSuite() != null) {
            dto.setSuiteId(testCase.getTestSuite().getId());
            if (testCase.getTestSuite().getProject() != null) {
                dto.setProjectId(testCase.getTestSuite().getProject().getId());
            }
        }

        return dto;
    }
}