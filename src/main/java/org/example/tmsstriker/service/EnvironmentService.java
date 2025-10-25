package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class EnvironmentService {

    private final EnvironmentRepository repository;
    private final ProjectRepository projectRepository;

    public List<EnvironmentDTO> getByProject(UUID projectId) {
        return repository.findByProjectId(projectId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

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
        log.debug("Creating environment with DTO: {}", dto);

        // Валідація обов'язкових полів
        if (dto.getProjectId() == null) {
            throw new ApiException("Project ID is required", HttpStatus.BAD_REQUEST);
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ApiException("Environment name is required", HttpStatus.BAD_REQUEST);
        }

        Environment entity = new Environment();
        entity.setId(UUID.randomUUID());

        // Знайти та встановити проект
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ApiException("Project not found: " + dto.getProjectId(), HttpStatus.NOT_FOUND));
        entity.setProject(project);

        // Встановити інші поля
        entity.setName(dto.getName().trim());
        entity.setSlug(dto.getSlug());
        entity.setDescription(dto.getDescription());
        entity.setHost(dto.getHost());
        entity.setPort(dto.getPort() != null ? dto.getPort() : 0); // Default port = 0

        log.debug("Saving environment: {}", entity);
        Environment saved = repository.save(entity);
        log.debug("Environment saved with ID: {}", saved.getId());

        return toDto(saved);
    }

    public EnvironmentDTO update(UUID id, EnvironmentDTO dto) {
        Environment entity = repository.findById(id)
                .orElseThrow(() -> new ApiException("Environment not found", HttpStatus.NOT_FOUND));

        if (dto.getProjectId() != null && !dto.getProjectId().equals(entity.getProject().getId())) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));
            entity.setProject(project);
        }

        if (dto.getName() != null) entity.setName(dto.getName().trim());
        if (dto.getSlug() != null) entity.setSlug(dto.getSlug());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getHost() != null) entity.setHost(dto.getHost());
        if (dto.getPort() != null) entity.setPort(dto.getPort());

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