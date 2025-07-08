package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.EnvironmentDTO;
import org.example.tmsstriker.entity.Environment;
import org.example.tmsstriker.entity.Project;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.repository.EnvironmentRepository;
import org.example.tmsstriker.repository.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnvironmentService {

    private final EnvironmentRepository repository;
    private final ProjectRepository projectRepository;

    public List<EnvironmentDTO> getByProject(UUID projectId) {
        return repository.findByProjectId(projectId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Додати ці методи ПІСЛЯ getByProject() в EnvironmentService:

    public List<EnvironmentDTO> getAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public EnvironmentDTO getById(UUID id) {
        Environment entity = repository.findById(id)
                .orElseThrow(() -> new ApiException("Environment not found: " + id, HttpStatus.NOT_FOUND));
        return toDto(entity);
    }

    public EnvironmentDTO create(EnvironmentDTO dto) {
        Environment entity = new Environment();
        entity.setId(UUID.randomUUID());
        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));
            entity.setProject(project);
        }
        entity.setName(dto.getName());
        entity.setSlug(dto.getSlug());
        entity.setDescription(dto.getDescription());
        entity.setHost(dto.getHost());
        entity.setPort(dto.getPort());
        return toDto(repository.save(entity));
    }

    public EnvironmentDTO update(UUID id, EnvironmentDTO dto) {
        Environment entity = repository.findById(id)
                .orElseThrow(() -> new ApiException("Environment not found", HttpStatus.NOT_FOUND));
        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));
            entity.setProject(project);
        }
        entity.setName(dto.getName());
        entity.setSlug(dto.getSlug());
        entity.setDescription(dto.getDescription());
        entity.setHost(dto.getHost());
        entity.setPort(dto.getPort());
        return toDto(repository.save(entity));
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ApiException("Environment not found", HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }

    private EnvironmentDTO toDto(Environment entity) {
        EnvironmentDTO dto = new EnvironmentDTO();
        dto.setId(entity.getId());
        dto.setProjectId(entity.getProject() != null ? entity.getProject().getId() : null);
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        dto.setDescription(entity.getDescription());
        dto.setHost(entity.getHost());
        dto.setPort(entity.getPort());
        return dto;
    }
}
