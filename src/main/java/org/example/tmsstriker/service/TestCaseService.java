package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.example.tmsstriker.dto.*;
import org.example.tmsstriker.entity.*;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.repository.TestCaseRepository;
import org.example.tmsstriker.repository.TestSuiteRepository;
import org.example.tmsstriker.service.spec.TestCaseSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestCaseService {

    private final TestCaseRepository repo;
    private final TestSuiteRepository suiteRepo;
    private final CodeGeneratorService codeGeneratorService;

    public TestCaseDTO getById(UUID id) {
        return repo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ApiException("TestCase not found: " + id, HttpStatus.NOT_FOUND));
    }

    public Page<TestCaseDTO> getCases(UUID suiteId, String search, Pageable pg) {
        Specification<TestCase> spec = Specification
                .where(TestCaseSpecification.belongsToSuite(suiteId))
                .and(TestCaseSpecification.containsTextInAnyField(search));
        return repo.findAll(spec, pg).map(this::toDto);
    }

// В методі createTestCase змінити:

    @Transactional
    public TestCaseDTO createTestCase(TestCaseDTO dto) {
        if (dto.getSuiteId() == null) {
            throw new ApiException("Suite is required", HttpStatus.BAD_REQUEST);
        }
        if (repo.existsByTitleAndTestSuite_Id(dto.getTitle(), dto.getSuiteId())) {
            throw new ApiException(
                    "Duplicate Test Case title: '" + dto.getTitle() + "' in suite " + dto.getSuiteId(),
                    HttpStatus.CONFLICT
            );
        }
        TestCase e = toEntity(dto);

        // ВИПРАВЛЕНО: встановити suite та project
        TestSuite suite = suiteRepo.findById(dto.getSuiteId())
                .orElseThrow(() -> new ApiException("Suite not found: " + dto.getSuiteId(), HttpStatus.NOT_FOUND));
        e.setTestSuite(suite);

        // ДОДАНО: встановити project від suite
        if (suite.getProject() != null) {
            e.setProject(suite.getProject());
        }

        if (e.getCode() == null || e.getCode().isEmpty()) {
            UUID projectId = getProjectId(dto, e);
            String code = codeGeneratorService.generateNextCode("test_case", projectId, "TC-");
            e.setCode(code);
        }

        return toDto(repo.save(e));
    }

    @Transactional
    public TestCaseDTO updateTestCase(UUID id, TestCaseDTO dto) {
        TestCase e = repo.findById(id)
                .orElseThrow(() -> new ApiException("Not found: " + id, HttpStatus.NOT_FOUND));
        e.setTitle(dto.getTitle());
        e.setPreconditions(dto.getPreconditions());
        e.setDescription(dto.getDescription());
        updateSteps(e, dto.getSteps());
        e.setPriority(dto.getPriority());
        e.setTags(dto.getTags());
        e.setState(dto.getState());
        e.setOwner(dto.getOwner());
        e.setType(dto.getType());
        e.setAutomationStatus(dto.getAutomationStatus());
        e.setComponent(dto.getComponent());
        e.setUseCase(dto.getUseCase());
        e.setRequirement(dto.getRequirement());
        if (dto.getCode() != null) {
            e.setCode(dto.getCode());
        }
        if (dto.getSuiteId() != null) {
            suiteRepo.findById(dto.getSuiteId()).ifPresent(e::setTestSuite);
        } else {
            e.setTestSuite(null);
        }
        return toDto(repo.save(e));
    }

    private void updateSteps(TestCase entity, List<TestStepDTO> newSteps) {
        List<TestStep> existing = entity.getSteps();
        if (existing == null) {
            existing = new ArrayList<>();
            entity.setSteps(existing);
        }
        existing.removeIf(oldStep -> newSteps == null ||
                newSteps.stream().noneMatch(dto -> dto.getId() != null && dto.getId().equals(oldStep.getId())));
        if (newSteps != null) {
            for (int i = 0; i < newSteps.size(); i++) {
                TestStepDTO stepDto = newSteps.get(i);
                TestStep step = null;
                if (stepDto.getId() != null) {
                    step = existing.stream()
                            .filter(s -> s.getId() != null && s.getId().equals(stepDto.getId()))
                            .findFirst().orElse(null);
                }
                if (step == null) {
                    step = new TestStep();
                    step.setTestCase(entity);
                    existing.add(step);
                }
                step.setOrderIndex(stepDto.getOrderIndex() != null ? stepDto.getOrderIndex() : i);
                step.setAction(stepDto.getAction());
                step.setExpectedResult(stepDto.getExpectedResult());
            }
        }
    }

    @Transactional
    public void deleteTestCase(UUID id) {
        repo.deleteById(id);
    }

    @Transactional
    public void bulkUpdate(BulkTestCaseRequestDTO req) {
        var cases = req.getIds().stream()
                .map(repo::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (req.isDelete()) {
            cases.forEach(repo::delete);
            return;
        }

        if (req.getMoveToSuiteId() != null) {
            var dest = suiteRepo.findById(req.getMoveToSuiteId()).orElse(null);
            if (dest != null) {
                cases.forEach(tc -> {
                    tc.setTestSuite(dest);
                    repo.save(tc);
                });
            }
        }

        if (req.getCopyToSuiteId() != null) {
            var dest = suiteRepo.findById(req.getCopyToSuiteId()).orElse(null);
            if (dest != null) {
                UUID projectId = dest.getProject() != null ? dest.getProject().getId() : null;
                for (TestCase orig : cases) {
                    TestCase cp = new TestCase();
                    copyFields(cp, orig);
                    cp.setTestSuite(dest);
                    if (projectId != null) {
                        cp.setProject(new Project());
                        cp.getProject().setId(projectId);
                    }
                    String code = codeGeneratorService.generateNextCode("test_case", projectId, "TC-");
                    cp.setCode(code);
                    if (orig.getSteps() != null) {
                        for (TestStep step : orig.getSteps()) {
                            TestStep stepCopy = new TestStep();
                            stepCopy.setAction(step.getAction());
                            stepCopy.setExpectedResult(step.getExpectedResult());
                            stepCopy.setOrderIndex(step.getOrderIndex());
                            cp.addStep(stepCopy);
                        }
                    }
                    repo.save(cp);
                }
            }
        }
    }

    @Transactional
    public ImportResultDto importFromCsv(UUID suiteId, InputStream in) throws Exception {
        ImportResultDto result = new ImportResultDto();
        CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(new InputStreamReader(in));
        int row = 1;
        for (var rec : parser) {
            try {
                TestCase tc = new TestCase();
                suiteRepo.findById(suiteId).ifPresent(tc::setTestSuite);
                tc.setTitle(rec.get("title"));
                tc.setPreconditions(rec.get("preconditions"));
                tc.setDescription(rec.get("description"));
                tc.setPriority(rec.get("priority"));
                tc.setTags(rec.get("tags"));
                tc.setState(rec.get("state"));
                tc.setOwner(rec.get("owner"));
                tc.setType(rec.get("type"));
                tc.setAutomationStatus(rec.get("automationStatus"));
                tc.setComponent(rec.get("component"));
                tc.setUseCase(rec.get("useCase"));
                tc.setRequirement(rec.get("requirement"));
                UUID projectId = tc.getTestSuite() != null && tc.getTestSuite().getProject() != null
                        ? tc.getTestSuite().getProject().getId() : null;
                String code = codeGeneratorService.generateNextCode("test_case", projectId, "TC-");
                tc.setCode(code);
                repo.save(tc);
                result.incrementCreated();
            } catch (Exception ex) {
                result.addError("Row " + row + ": " + ex.getMessage());
            }
            row++;
        }
        return result;
    }

    private UUID getProjectId(TestCaseDTO dto, TestCase entity) {
        if (dto.getProjectId() != null) return dto.getProjectId();
        if (entity.getTestSuite() != null && entity.getTestSuite().getProject() != null)
            return entity.getTestSuite().getProject().getId();
        throw new ApiException("Project ID required for code generation", HttpStatus.BAD_REQUEST);
    }

    private TestCaseDTO toDto(TestCase e) {
        TestCaseDTO dto = new TestCaseDTO();
        dto.setId(e.getId());
        dto.setCode(e.getCode());
        dto.setTitle(e.getTitle());
        dto.setPreconditions(e.getPreconditions());
        dto.setDescription(e.getDescription());
        dto.setSteps(e.getSteps() == null ? null : e.getSteps().stream().map(this::toTestStepDTO).collect(Collectors.toList()));
        dto.setPriority(e.getPriority());
        dto.setTags(e.getTags());
        dto.setState(e.getState());
        dto.setOwner(e.getOwner());
        dto.setType(e.getType());
        dto.setAutomationStatus(e.getAutomationStatus());
        dto.setComponent(e.getComponent());
        dto.setUseCase(e.getUseCase());
        dto.setRequirement(e.getRequirement());
        dto.setSuiteId(e.getTestSuite() != null ? e.getTestSuite().getId() : null);
        dto.setProjectId(e.getTestSuite() != null && e.getTestSuite().getProject() != null ? e.getTestSuite().getProject().getId() : null);
        return dto;
    }

    private TestCase toEntity(TestCaseDTO dto) {
        TestCase e = new TestCase();
        if (dto.getId() != null) e.setId(dto.getId());
        if (dto.getCode() != null) e.setCode(dto.getCode());
        e.setTitle(dto.getTitle());
        e.setPreconditions(dto.getPreconditions());
        e.setDescription(dto.getDescription());
        e.setPriority(dto.getPriority());
        e.setTags(dto.getTags());
        e.setState(dto.getState());
        e.setOwner(dto.getOwner());
        e.setType(dto.getType());
        e.setAutomationStatus(dto.getAutomationStatus());
        e.setComponent(dto.getComponent());
        e.setUseCase(dto.getUseCase());
        e.setRequirement(dto.getRequirement());
        if (dto.getProjectId() != null) {
            Project project = new Project();
            project.setId(dto.getProjectId());
            e.setProject(project);
        }
        if (dto.getSuiteId() != null) {
            TestSuite suite = new TestSuite();
            suite.setId(dto.getSuiteId());
            e.setTestSuite(suite);
        }
        if (dto.getSteps() != null) {
            List<TestStep> steps = dto.getSteps().stream().map(this::toTestStepEntity).collect(Collectors.toList());
            e.setSteps(steps);
            for (TestStep s : steps) {
                s.setTestCase(e);
            }
        }
        return e;
    }

    private TestStep toTestStepEntity(TestStepDTO dto) {
        TestStep step = new TestStep();
        step.setId(dto.getId());
        step.setOrderIndex(dto.getOrderIndex());
        step.setAction(dto.getAction());
        step.setExpectedResult(dto.getExpectedResult());
        return step;
    }

    private TestStepDTO toTestStepDTO(TestStep step) {
        TestStepDTO dto = new TestStepDTO();
        dto.setId(step.getId());
        dto.setOrderIndex(step.getOrderIndex());
        dto.setAction(step.getAction());
        dto.setExpectedResult(step.getExpectedResult());
        return dto;
    }

    private void copyFields(TestCase dest, TestCase orig) {
        dest.setTitle(orig.getTitle());
        dest.setPreconditions(orig.getPreconditions());
        dest.setDescription(orig.getDescription());
        dest.setPriority(orig.getPriority());
        dest.setTags(orig.getTags());
        dest.setState(orig.getState());
        dest.setOwner(orig.getOwner());
        dest.setType(orig.getType());
        dest.setAutomationStatus(orig.getAutomationStatus());
        dest.setComponent(orig.getComponent());
        dest.setUseCase(orig.getUseCase());
        dest.setRequirement(orig.getRequirement());
    }
}
