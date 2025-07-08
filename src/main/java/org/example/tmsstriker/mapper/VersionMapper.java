package org.example.tmsstriker.mapper;

import org.example.tmsstriker.dto.VersionDTO;
import org.example.tmsstriker.entity.Version;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for converting between Version entity and DTO.
 * Handles project association and ignores unmapped fields.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface VersionMapper {
    VersionMapper INSTANCE = Mappers.getMapper(VersionMapper.class);

    /**
     * Convert Version entity to DTO.
     * Maps project.id to projectId.
     */
    @Mapping(source = "project.id", target = "projectId")
    VersionDTO toDto(Version entity);

    /**
     * Convert DTO to entity for creation.
     * Ignores project (set in service layer) and id is left to service.
     */
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "id", ignore = true)
    Version toEntity(VersionDTO dto);

    /**
     * Update an existing entity from DTO.
     * Ignores project association and id.
     * Implemented here as default method to ensure compilation.
     */
    default void updateEntityFromDto(VersionDTO dto, @MappingTarget Version entity) {
        if (dto == null || entity == null) {
            return;
        }
        // manually map fields
        entity.setTitle(dto.getTitle());
        entity.setSlug(dto.getSlug());
        entity.setDescription(dto.getDescription());
        // project and id are managed separately
    }
}



