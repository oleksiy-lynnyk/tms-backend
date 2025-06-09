// src/main/java/org/example/tmsstriker/service/ConfigurationService.java
package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.ConfigurationDTO;
import org.example.tmsstriker.entity.Configuration;
import org.example.tmsstriker.repository.ConfigurationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private final ConfigurationRepository repository;

    public List<ConfigurationDTO> getByProject(UUID projectId) {
        return repository.findByProjectId(projectId).stream()
                .map(this::toDto)
                .toList();
    }

    public ConfigurationDTO create(ConfigurationDTO dto) {
        Configuration entity = new Configuration();
        entity.setId(UUID.randomUUID());
        mapFields(dto, entity);
        return toDto(repository.save(entity));
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private void mapFields(ConfigurationDTO dto, Configuration entity) {
        entity.setProjectId(dto.getProjectId());
        entity.setTitle(dto.getTitle());
        entity.setSlug(dto.getSlug());
        entity.setDescription(dto.getDescription());
        entity.setOs(dto.getOs());
        entity.setBrowser(dto.getBrowser());
        entity.setDevice(dto.getDevice());
    }

    private ConfigurationDTO toDto(Configuration entity) {
        ConfigurationDTO dto = new ConfigurationDTO();
        dto.setId(entity.getId());
        dto.setProjectId(entity.getProjectId());
        dto.setTitle(entity.getTitle());
        dto.setSlug(entity.getSlug());
        dto.setDescription(entity.getDescription());
        dto.setOs(entity.getOs());
        dto.setBrowser(entity.getBrowser());
        dto.setDevice(entity.getDevice());
        return dto;
    }
}
