package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.EnvironmentDTO;
import org.example.tmsstriker.entity.Environment;
import org.example.tmsstriker.repository.EnvironmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnvironmentService {
    private final EnvironmentRepository repository;

    public List<EnvironmentDTO> getByProject(UUID projectId) {
        return repository.findByProjectId(projectId).stream()
                .map(this::toDto)
                .toList();
    }

    public EnvironmentDTO create(EnvironmentDTO dto) {
        Environment entity = new Environment();
        entity.setId(UUID.randomUUID());
        mapFields(dto, entity);
        return toDto(repository.save(entity));
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private void mapFields(EnvironmentDTO dto, Environment entity) {
        entity.setProjectId(dto.getProjectId());
        entity.setTitle(dto.getTitle());
        entity.setSlug(dto.getSlug());
        entity.setDescription(dto.getDescription());
        entity.setHost(dto.getHost());
    }

    private EnvironmentDTO toDto(Environment entity) {
        EnvironmentDTO dto = new EnvironmentDTO();
        dto.setId(entity.getId());
        dto.setProjectId(entity.getProjectId());
        dto.setTitle(entity.getTitle());
        dto.setSlug(entity.getSlug());
        dto.setDescription(entity.getDescription());
        dto.setHost(entity.getHost());
        return dto;
    }


}
