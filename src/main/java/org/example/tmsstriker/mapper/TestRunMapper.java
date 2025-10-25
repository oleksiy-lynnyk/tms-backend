package org.example.tmsstriker.mapper;

import org.example.tmsstriker.dto.TestRunDTO;
import org.example.tmsstriker.entity.TestRun;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TestRunMapper {
    @Mapping(target = "projectId", expression = "java(entity.getProject() != null ? entity.getProject().getId() : null)")
    @Mapping(target = "assignedTo", expression = "java(entity.getAssignedTo() != null ? entity.getAssignedTo().getId() : null)")
    @Mapping(target = "assignedToName", expression = "java(entity.getAssignedTo() != null ? entity.getAssignedTo().getFullName() : null)")
    @Mapping(target = "testCaseIds", expression = "java(entity.getTestCases() != null ? entity.getTestCases().stream().map(tc -> tc.getId()).toList() : null)")
    @Mapping(target = "environmentIds", expression = "java(entity.getEnvironments() != null ? entity.getEnvironments().stream().map(env -> env.getId()).toList() : null)")
    @Mapping(target = "configurationId", expression = "java(entity.getConfiguration() != null ? entity.getConfiguration().getId() : null)")
    @Mapping(target = "versionId", expression = "java(entity.getVersion() != null ? entity.getVersion().getId() : null)")
    @Mapping(target = "environmentNames", expression = "java(entity.getEnvironments() != null ? entity.getEnvironments().stream().map(env -> env.getName()).toList() : null)")
    @Mapping(target = "configurationName", expression = "java(entity.getConfiguration() != null ? entity.getConfiguration().getName() : null)")
    @Mapping(target = "versionName", expression = "java(entity.getVersion() != null ? entity.getVersion().getName() : null)")
    @Mapping(target = "testCaseTitles", expression = "java(entity.getTestCases() != null ? entity.getTestCases().stream().map(tc -> tc.getTitle()).toList() : null)")
    TestRunDTO toDto(TestRun entity);

    // DTO → Entity (часто для створення/оновлення — потрібна ручна логіка в сервісі!)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "testCases", ignore = true)
    @Mapping(target = "environments", ignore = true)
    @Mapping(target = "configuration", ignore = true)
    @Mapping(target = "version", ignore = true)
    TestRun toEntity(TestRunDTO dto);
}
