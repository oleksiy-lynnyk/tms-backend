package org.example.tmsstriker.mapper;

import org.example.tmsstriker.dto.VersionDTO;
import org.example.tmsstriker.entity.Version;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface VersionMapper {
    VersionMapper INSTANCE = Mappers.getMapper(VersionMapper.class);

    /**
     * Convert Version entity to DTO.
     * Maps project.id to projectId and title to title.
     */
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "title", target = "title")  //
    VersionDTO toDto(Version entity);

    /**
     * Convert DTO to entity for creation.
     * Maps title to both title and name fields.
     */
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "title", target = "title")  //
    @Mapping(source = "title", target = "name")   //
    Version toEntity(VersionDTO dto);

    /**
     * Update an existing entity from DTO.
     */
    default void updateEntityFromDto(VersionDTO dto, @MappingTarget Version entity) {
        if (dto == null || entity == null) {
            return;
        }
        // ✅ Виправлено: мапимо title в обидва поля
        entity.setTitle(dto.getTitle());
        entity.setName(dto.getTitle());    // ✅ ДОДАНО: name = title
        entity.setSlug(dto.getSlug());
        entity.setDescription(dto.getDescription());
    }
}
