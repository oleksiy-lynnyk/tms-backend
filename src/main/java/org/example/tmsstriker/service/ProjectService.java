package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.ProjectDTO;
import org.example.tmsstriker.entity.Project;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.repository.ProjectRepository;
import org.example.tmsstriker.repository.TestCaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;
    private final TestCaseRepository testCaseRepository;
    private final CodeGeneratorService codeGeneratorService;

    @Transactional(readOnly = true)
    public List<ProjectDTO> getAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Знаходить проєкт за ідентифікатором та повертає Optional DTO.
     * Використовується в контролері для {@code GET /api/projects/{id}}.
     */
    @Transactional(readOnly = true)
    public Optional<ProjectDTO> findById(UUID id) {
        return repository.findById(id)
                .map(this::toDto);
    }

    @Transactional
    public ProjectDTO create(ProjectDTO dto) {
        // --- Дублікати імені проекту ---
        if (repository.existsByName(dto.getName())) {
            throw new ApiException(
                    "Duplicate project name: '" + dto.getName() + "'",
                    HttpStatus.CONFLICT
            );
        }

        Project entity = new Project();
        // НЕ ВСТАНОВЛЮЙТЕ ID ТУТ
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCode(codeGeneratorService.generateNextCode("project", null, "PR-"));

        return toDto(repository.save(entity));
    }

    @Transactional
    public ProjectDTO update(UUID id, ProjectDTO dto) {
        Project entity = repository.findById(id)
                .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        // code не змінюємо вручну

        Project updated = repository.save(entity);
        return toDto(updated);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ApiException("Project not found", HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }

    private ProjectDTO toDto(Project entity) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setTestCasesCount((int) testCaseRepository.countByProjectId(entity.getId()));
        return dto;
    }
}
