package org.example.tmsstriker.mapper;

import org.example.tmsstriker.dto.TestRunCaseResultAuditDTO;
import org.example.tmsstriker.entity.TestRunCaseResultAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TestRunCaseResultAuditMapper {
    @Mapping(target = "resultId", expression = "java(entity.getResult() != null ? entity.getResult().getId() : null)")
    @Mapping(target = "changedBy", expression = "java(entity.getChangedBy() != null ? entity.getChangedBy().getId() : null)")
    @Mapping(target = "changedByName", expression = "java(entity.getChangedBy() != null ? entity.getChangedBy().getName() : null)")
    TestRunCaseResultAuditDTO toDto(TestRunCaseResultAudit entity);
}
