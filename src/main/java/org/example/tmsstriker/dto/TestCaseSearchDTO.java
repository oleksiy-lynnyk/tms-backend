package org.example.tmsstriker.dto;

import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO for test case search parameters
 */
@Data
@Builder
@Schema(description = "Parameters for advanced test case search")
public class TestCaseSearchDTO {

    @Schema(description = "Project ID to search within", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5")
    private UUID projectId;

    @Schema(description = "Test suite ID to search within")
    private UUID suiteId;

    @Schema(description = "Search text in title and description", example = "login functionality")
    private String query;

    @Schema(description = "Filter by priority", allowableValues = {"High", "Medium", "Low", "Critical"})
    private String priority;

    @Schema(description = "Filter by status", allowableValues = {"Active", "Draft", "Deprecated"})
    private String status;

    @Schema(description = "Filter by automation status", allowableValues = {"Manual", "Automated", "Mixed"})
    private String automationStatus;

    @Schema(description = "Filter by owner", example = "john.doe")
    private String owner;

    @Schema(description = "Filter by tags (comma-separated)", example = "smoke,regression,api")
    private String tags;

    @Schema(description = "Filter by component", example = "Authentication")
    private String component;

    @Schema(description = "Filter by type", allowableValues = {"Functional", "Performance", "Security", "API"})
    private String type;
}