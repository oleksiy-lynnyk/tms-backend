package org.example.tmsstriker.dto;

import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for test case statistics
 */
@Data
@Builder
@Schema(description = "Test case statistics for a project")
public class TestCaseStatsDTO {

    @Schema(description = "Total number of test cases", example = "150")
    private Integer totalTestCases;

    @Schema(description = "Number of automated tests", example = "90")
    private Integer automatedCount;

    @Schema(description = "Number of manual tests", example = "60")
    private Integer manualCount;

    @Schema(description = "Automation coverage percentage", example = "60.0")
    private Double automationCoverage;

    @Schema(description = "Number of active tests", example = "140")
    private Integer activeCount;

    @Schema(description = "Number of deprecated tests", example = "10")
    private Integer deprecatedCount;

    @Schema(description = "Breakdown by priority")
    private PriorityBreakdownDTO priorityBreakdown;

    @Schema(description = "Breakdown by status")
    private StatusBreakdownDTO statusBreakdown;

    @Schema(description = "Average steps per test case", example = "5.2")
    private Double avgStepsPerTest;
}

/**
 * Test case breakdown by priority
 */
@Data
@Builder
@Schema(description = "Test case breakdown by priority")
class PriorityBreakdownDTO {

    @Schema(description = "Critical priority tests", example = "15")
    private Integer critical;

    @Schema(description = "High priority tests", example = "45")
    private Integer high;

    @Schema(description = "Medium priority tests", example = "70")
    private Integer medium;

    @Schema(description = "Low priority tests", example = "20")
    private Integer low;
}

/**
 * Test case breakdown by status
 */
@Data
@Builder
@Schema(description = "Test case breakdown by status")
class StatusBreakdownDTO {

    @Schema(description = "Active tests", example = "120")
    private Integer active;

    @Schema(description = "Draft tests", example = "20")
    private Integer draft;

    @Schema(description = "Deprecated tests", example = "8")
    private Integer deprecated;

    @Schema(description = "Reviewed tests", example = "2")
    private Integer reviewed;
}
