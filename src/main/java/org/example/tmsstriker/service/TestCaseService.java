package org.example.tmsstriker.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.example.tmsstriker.dto.BulkTestCaseRequestDTO;
import org.example.tmsstriker.dto.ImportResultDto;
import org.example.tmsstriker.dto.TestCaseDTO;
import org.example.tmsstriker.dto.TestStepDTO;
import org.example.tmsstriker.entity.TestCase;
import org.example.tmsstriker.entity.TestStep;
import org.example.tmsstriker.entity.TestSuite;
import org.example.tmsstriker.entity.ProjectCaseSequence;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.repository.TestCaseRepository;
import org.example.tmsstriker.repository.TestSuiteRepository;
import org.example.tmsstriker.repository.ProjectCaseSequenceRepository;
import org.example.tmsstriker.service.spec.TestCaseSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TestCaseService {
    private final TestCaseRepository repo;
    private final TestSuiteRepository suiteRepo;
    private final ProjectCaseSequenceRepository sequenceRepo;

    public TestCaseService(
            TestCaseRepository repo,
            TestSuiteRepository suiteRepo,
            ProjectCaseSequenceRepository sequenceRepo
    ) {
        this.repo = repo;
        this.suiteRepo = suiteRepo;
        this.sequenceRepo = sequenceRepo;
    }

    public TestCaseDTO getById(UUID id) {
        return repo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ApiException("TestCase not found: " + id, HttpStatus.NOT_FOUND));
    }

    public Page<TestCaseDTO> getCases(UUID suiteId, String search, Pageable pg) {
        Specification<TestCase> spec = Specification
                .where(TestCaseSpecification.belongsToSuite(suiteId))
                .and(TestCaseSpecification.containsTextInAnyField(search));
        return repo.findAll(spec, pg)
                .map(this::toDto);
    }

    @Transactional
    public TestCaseDTO createTestCase(TestCaseDTO dto) {
        TestCase e = toEntity(dto);

        if (dto.getSuiteId() == null) {
            throw new ApiException("Suite is required", HttpStatus.BAD_REQUEST);
        }
        suiteRepo.findById(dto.getSuiteId()).ifPresent(e::setTestSuite);

        // Генеруємо code тільки для нових кейсів, якщо він не вказаний
        if (e.getCode() == null || e.getCode().isEmpty()) {
            UUID projectId = dto.getProjectId() != null
                    ? dto.getProjectId()
                    : (e.getTestSuite() != null && e.getTestSuite().getProjectId() != null)
                    ? e.getTestSuite().getProjectId()
                    : null;
            if (projectId == null) {
                throw new ApiException("Project ID required for code generation", HttpStatus.BAD_REQUEST);
            }
            int nextNum = getNextCaseNumberAtomic(projectId);
            String code = "TC-" + nextNum;
            e.setCode(code);
        }

        // Додаємо кроки для нового кейсу
        if (dto.getSteps() != null) {
            for (int i = 0; i < dto.getSteps().size(); i++) {
                TestStepDTO stepDto = dto.getSteps().get(i);
                TestStep step = toTestStepEntity(stepDto);
                step.setOrderIndex(stepDto.getOrderIndex() != null ? stepDto.getOrderIndex() : i);
                e.addStep(step); // гарантія коректного parent/id
            }
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
        // Дозволяємо редагувати code (НЕ рекомендується міняти через фронт)
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

    /** Головний метод для коректного оновлення списку кроків (steps) */
    private void updateSteps(TestCase entity, List<TestStepDTO> newSteps) {
        List<TestStep> existing = entity.getSteps();
        // 1. Видалити ті, що були, але їх нема в новому списку
        existing.removeIf(oldStep ->
                newSteps == null ||
                        newSteps.stream().noneMatch(dto -> dto.getId() != null && dto.getId().equals(oldStep.getId()))
        );
        // 2. Додати/оновити кроки з newSteps
        if (newSteps != null) {
            for (int i = 0; i < newSteps.size(); i++) {
                TestStepDTO stepDto = newSteps.get(i);
                TestStep step = null;
                if (stepDto.getId() != null) {
                    step = existing.stream()
                            .filter(s -> s.getId() != null && s.getId().equals(stepDto.getId()))
                            .findFirst()
                            .orElse(null);
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
                cases.forEach(tc -> { tc.setTestSuite(dest); repo.save(tc); });
            }
        }

        if (req.getCopyToSuiteId() != null) {
            var dest = suiteRepo.findById(req.getCopyToSuiteId()).orElse(null);
            if (dest != null) {
                UUID projectId = dest.getProjectId();
                for (TestCase orig : cases) {
                    TestCase cp = new TestCase();
                    cp.copyFieldsFrom(orig);
                    cp.setTestSuite(dest);
                    cp.setProjectId(projectId);
                    int nextNum = getNextCaseNumberAtomic(projectId);
                    cp.setCode("TC-" + nextNum);

                    // Гарантуємо що id та parent виставлені коректно:
                    if (orig.getSteps() != null) {
                        for (TestStep step : orig.getSteps()) {
                            TestStep stepCopy = new TestStep();
                            stepCopy.setAction(step.getAction());
                            stepCopy.setExpectedResult(step.getExpectedResult());
                            stepCopy.setOrderIndex(step.getOrderIndex());
                            cp.addStep(stepCopy); // id буде null і буде проставлено parent!
                        }
                    }
                    repo.save(cp);
                }
            }
        }

        if (req.getOperations() != null) {
            cases.forEach(tc ->
                    req.getOperations().forEach((field, op) ->
                            tc.applyFieldOperation(field, op)
                    )
            );
            repo.saveAll(cases);
        }
    }

    @Transactional
    public ImportResultDto importFromCsv(UUID suiteId, InputStream in) throws Exception {
        ImportResultDto result = new ImportResultDto();
        CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader()
                .parse(new InputStreamReader(in));
        int row = 1;
        for (var rec : parser) {
            try {
                TestCase tc = new TestCase();
                suiteRepo.findById(suiteId).ifPresent(tc::setTestSuite);
                tc.setTitle(rec.get("title"));
                tc.setPreconditions(rec.get("preconditions"));
                tc.setDescription(rec.get("description"));
                // ! Кроки потрібно парсити окремо (JSON, роздільник — як домовлено)
                // tc.setSteps(rec.get("steps"));
                // expectedResult -- ігноруємо (тільки на рівні кроку)
                tc.setPriority(rec.get("priority"));
                tc.setTags(rec.get("tags"));
                tc.setState(rec.get("state"));
                tc.setOwner(rec.get("owner"));
                tc.setType(rec.get("type"));
                tc.setAutomationStatus(rec.get("automationStatus"));
                tc.setComponent(rec.get("component"));
                tc.setUseCase(rec.get("useCase"));
                tc.setRequirement(rec.get("requirement"));

                UUID projectId = tc.getTestSuite().getProjectId();
                int nextNum = getNextCaseNumberAtomic(projectId);
                String code = "TC-" + nextNum;
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

    // --- Атомарний генератор номера для code (унікальний в рамках проекту) ---
    @Transactional
    protected int getNextCaseNumberAtomic(UUID projectId) {
        ProjectCaseSequence seq = sequenceRepo.findByProjectIdForUpdate(projectId)
                .orElseGet(() -> {
                    ProjectCaseSequence s = new ProjectCaseSequence();
                    s.setProjectId(projectId);
                    Integer maxNum = repo.findMaxCodeNumberByProject(projectId);
                    s.setNextValue(maxNum != null ? maxNum + 1 : 1);
                    return s;
                });
        int current = seq.getNextValue();
        seq.setNextValue(current + 1);
        sequenceRepo.save(seq);
        return current;
    }

    private TestCaseDTO toDto(TestCase e) {
        TestCaseDTO dto = new TestCaseDTO();
        dto.setId(e.getId());
        dto.setCode(e.getCode());
        dto.setTitle(e.getTitle());
        dto.setPreconditions(e.getPreconditions());
        dto.setDescription(e.getDescription());
        dto.setSteps(e.getSteps() == null ? null :
                e.getSteps().stream()
                        .map(this::toTestStepDTO)
                        .collect(Collectors.toList())
        );
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
        dto.setProjectId(
                e.getTestSuite() != null && e.getTestSuite().getProjectId() != null
                        ? e.getTestSuite().getProjectId()
                        : null
        );
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
        if (dto.getProjectId() != null) e.setProjectId(dto.getProjectId());
        // Додаємо кроки (для create)
        if (dto.getSteps() != null) {
            List<TestStep> steps = dto.getSteps().stream()
                    .map(this::toTestStepEntity)
                    .collect(Collectors.toList());
            for (TestStep s : steps) {
                e.addStep(s); // гарантія null id та parent
            }
        }
        return e;
    }

    // --- Маппери для кроків ---
    private TestStep toTestStepEntity(TestStepDTO dto) {
        TestStep step = new TestStep();
        // id не ставимо взагалі для нових, лише для update
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
}
