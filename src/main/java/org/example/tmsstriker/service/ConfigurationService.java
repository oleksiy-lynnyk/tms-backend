package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.ConfigurationDTO;
import org.example.tmsstriker.entity.Configuration;
import org.example.tmsstriker.entity.Project;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.repository.ConfigurationRepository;
import org.example.tmsstriker.repository.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private final ConfigurationRepository repository;
    private final ProjectRepository projectRepository;

    public List<ConfigurationDTO> getByProject(UUID projectId) {
        return repository.findByProjectId(projectId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ConfigurationDTO> getAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ConfigurationDTO getById(UUID id) {
        Configuration entity = repository.findById(id)
                .orElseThrow(() -> new ApiException("Configuration not found: " + id, HttpStatus.NOT_FOUND));
        return toDto(entity);
    }

    public ConfigurationDTO create(ConfigurationDTO dto) {
        Configuration entity = new Configuration();
        entity.setId(UUID.randomUUID());
        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));
            entity.setProject(project);
        }
        entity.setName(dto.getName());
        entity.setSlug(dto.getSlug());
        entity.setDescription(dto.getDescription());
        entity.setOs(dto.getOs());
        entity.setBrowser(dto.getBrowser());
        entity.setDevice(dto.getDevice());
        return toDto(repository.save(entity));
    }

    public ConfigurationDTO update(UUID id, ConfigurationDTO dto) {
        Configuration entity = repository.findById(id)
                .orElseThrow(() -> new ApiException("Configuration not found", HttpStatus.NOT_FOUND));
        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));
            entity.setProject(project);
        }
        entity.setName(dto.getName());
        entity.setSlug(dto.getSlug());
        entity.setDescription(dto.getDescription());
        entity.setOs(dto.getOs());
        entity.setBrowser(dto.getBrowser());
        entity.setDevice(dto.getDevice());
        return toDto(repository.save(entity));
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ApiException("Configuration not found", HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }

    private ConfigurationDTO toDto(Configuration entity) {
        ConfigurationDTO dto = new ConfigurationDTO();
        dto.setId(entity.getId());
        dto.setProjectId(entity.getProject() != null ? entity.getProject().getId() : null);
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        dto.setDescription(entity.getDescription());
        dto.setOs(entity.getOs());
        dto.setBrowser(entity.getBrowser());
        dto.setDevice(entity.getDevice());
        return dto;
    }

}