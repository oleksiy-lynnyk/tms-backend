package org.example.tmsstriker.mapper;

import org.example.tmsstriker.dto.EnvironmentDTO;
import org.example.tmsstriker.entity.Environment;

/**
 * Utility class for converting between Environment entities and DTOs.
 */
public final class EnvironmentMapper {

    private EnvironmentMapper() { /* static only */ }

    /**
     * Converts an Environment entity to an EnvironmentDTO.
     */
    public static EnvironmentDTO toDto(Environment entity) {
        if (entity == null) {
            return null;
        }
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

    /**
     * Applies values from the DTO onto an existing Environment entity.
     * Useful for updates to avoid creating a new instance.
     */
    public static void updateEntityFromDto(EnvironmentDTO dto, Environment entity) {
        if (dto == null || entity == null) {
            return;
        }
        // project association should be set in service layer
        entity.setName(dto.getName());
        entity.setSlug(dto.getSlug());
        entity.setDescription(dto.getDescription());
        entity.setHost(dto.getHost());
        entity.setPort(dto.getPort());
    }
}

