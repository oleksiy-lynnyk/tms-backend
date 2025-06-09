package org.example.tmsstriker.service;

import org.example.tmsstriker.dto.ProjectDTO;
import org.example.tmsstriker.entity.Project;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ProjectService {
    private final ProjectRepository repo;
    private final CodeGeneratorService codeGeneratorService;

    public ProjectService(ProjectRepository repo, CodeGeneratorService codeGeneratorService) {
        this.repo = repo;
        this.codeGeneratorService = codeGeneratorService;
    }

    public Page<ProjectDTO> getPagedProjects(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return repo.findAll(pageable).map(this::toDto);
        } else {
            return repo.findByNameContainingIgnoreCase(search, pageable).map(this::toDto);
        }
    }

    public ProjectDTO findById(UUID id) {
        return repo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ApiException("Project not found: " + id, HttpStatus.NOT_FOUND));
    }

    public ProjectDTO create(ProjectDTO dto) {
        Project entity = toEntity(dto);
        entity.setId(UUID.randomUUID());

        // 🔢 Генерація унікального коду типу PR-1, PR-2...
        String code = codeGeneratorService.generateNextCode("project", null, "PR-");
        entity.setCode(code);

        Project saved = repo.save(entity);
        return toDto(saved);
    }

    public ProjectDTO update(UUID id, ProjectDTO dto) {
        Project entity = repo.findById(id)
                .orElseThrow(() -> new ApiException("Project not found: " + id, HttpStatus.NOT_FOUND));

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());

        // 👇 Ми НЕ оновлюємо код вручну
        // entity.setCode(dto.getCode());

        Project saved = repo.save(entity);
        return toDto(saved);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }

    private ProjectDTO toDto(Project e) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setDescription(e.getDescription());
        dto.setCode(e.getCode());
        return dto;
    }

    private Project toEntity(ProjectDTO dto) {
        Project e = new Project();
        if (dto.getId() != null) e.setId(dto.getId());
        e.setName(dto.getName());
        e.setDescription(dto.getDescription());
        // 👇 Ми ігноруємо dto.getCode()
        return e;
    }
}
