package org.example.tmsstriker.mapper;

import org.example.tmsstriker.dto.TestRunCaseResultDTO;
import org.example.tmsstriker.entity.TestRunCaseResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TestRunCaseResultMapper {

    @Mapping(target = "testRunId", expression = "java(entity.getTestRun() != null ? entity.getTestRun().getId() : null)")
    @Mapping(target = "testCaseId", expression = "java(entity.getTestCase() != null ? entity.getTestCase().getId() : null)")
    @Mapping(target = "testCaseTitle", expression = "java(entity.getTestCase() != null ? entity.getTestCase().getTitle() : null)")
    @Mapping(target = "executedBy", expression = "java(entity.getExecutedBy() != null ? entity.getExecutedBy().getId() : null)")
    @Mapping(target = "executedByName", expression = "java(entity.getExecutedBy() != null ? entity.getExecutedBy().getName() : null)")
    TestRunCaseResultDTO toDto(TestRunCaseResult entity);

    @Mapping(target = "testRun", ignore = true)
    @Mapping(target = "testCase", ignore = true)
    @Mapping(target = "executedBy", ignore = true)
    TestRunCaseResult toEntity(TestRunCaseResultDTO dto);
}
