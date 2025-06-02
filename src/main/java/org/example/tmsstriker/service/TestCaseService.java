package org.example.tmsstriker.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.example.tmsstriker.dto.BulkTestCaseRequestDTO;
import org.example.tmsstriker.dto.ImportResultDto;
import org.example.tmsstriker.dto.TestCaseDTO;
import org.example.tmsstriker.entity.TestCase;
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

        // --- CODE GEN START ---
        if (dto.getSuiteId() != null) {
            suiteRepo.findById(dto.getSuiteId()).ifPresent(e::setTestSuite);
        }
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
        // --- CODE GEN END ---

        return toDto(repo.save(e));
    }

    @Transactional
    public TestCaseDTO updateTestCase(UUID id, TestCaseDTO dto) {
        TestCase e = repo.findById(id)
                .orElseThrow(() -> new ApiException("Not found: " + id, HttpStatus.NOT_FOUND));
        e.setTitle(dto.getTitle());
        e.setPreconditions(dto.getPreconditions());
        e.setDescription(dto.getDescription());
        e.setSteps(dto.getSteps());
        e.setExpectedResult(dto.getExpectedResult());
        e.setPriority(dto.getPriority());
        e.setTags(dto.getTags());
        e.setState(dto.getState());
        e.setOwner(dto.getOwner());
        e.setType(dto.getType());
        e.setAutomationStatus(dto.getAutomationStatus());
        e.setComponent(dto.getComponent());
        e.setUseCase(dto.getUseCase());
        e.setRequirement(dto.getRequirement());
        // --- CODE GEN: Дозволяємо редагувати code (НЕ рекомендується міняти через фронт)
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
                    // --- CODE GEN: Унікальний code через sequence
                    int nextNum = getNextCaseNumberAtomic(projectId);
                    String code = "TC-" + nextNum;
                    cp.setCode(code);
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
                tc.setSteps(rec.get("steps"));
                tc.setExpectedResult(rec.get("expectedResult"));
                tc.setPriority(rec.get("priority"));
                tc.setTags(rec.get("tags"));
                tc.setState(rec.get("state"));
                tc.setOwner(rec.get("owner"));
                tc.setType(rec.get("type"));
                tc.setAutomationStatus(rec.get("automationStatus"));
                tc.setComponent(rec.get("component"));
                tc.setUseCase(rec.get("useCase"));
                tc.setRequirement(rec.get("requirement"));

                // --- CODE GEN при імпорті ---
                UUID projectId = tc.getTestSuite().getProjectId();
                int nextNum = getNextCaseNumberAtomic(projectId);
                String code = "TC-" + nextNum;
                tc.setCode(code);
                // --- END ---

                repo.save(tc);
                result.incrementCreated();
            } catch (Exception ex) {
                result.addError("Row " + row + ": " + ex.getMessage());
            }
            row++;
        }
        return result;
    }

    // --- Новий атомарний генератор номера для code (унікальний в рамках проекту) ---
    @Transactional
    protected int getNextCaseNumberAtomic(UUID projectId) {
        ProjectCaseSequence seq = sequenceRepo.findById(projectId)
                .orElseGet(() -> {
                    ProjectCaseSequence s = new ProjectCaseSequence();
                    s.setProjectId(projectId);
                    s.setNextValue(1);
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
        dto.setCode(e.getCode()); // --- CODE GEN: додати у DTO
        dto.setTitle(e.getTitle());
        dto.setPreconditions(e.getPreconditions());
        dto.setDescription(e.getDescription());
        dto.setSteps(e.getSteps());
        dto.setExpectedResult(e.getExpectedResult());
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
        // Якщо потрібно, додай ProjectId у DTO
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
        e.setSteps(dto.getSteps());
        e.setExpectedResult(dto.getExpectedResult());
        e.setPriority(dto.getPriority());
        e.setTags(dto.getTags());
        e.setState(dto.getState());
        e.setOwner(dto.getOwner());
        e.setType(dto.getType());
        e.setAutomationStatus(dto.getAutomationStatus());
        e.setComponent(dto.getComponent());
        e.setUseCase(dto.getUseCase());
        e.setRequirement(dto.getRequirement());
        return e;
    }
}
