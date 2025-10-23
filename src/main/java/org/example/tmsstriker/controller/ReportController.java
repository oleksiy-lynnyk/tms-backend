package org.example.tmsstriker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.reports.TestRunSummaryDTO;
import org.example.tmsstriker.dto.reports.ProjectOverviewDTO;
import org.example.tmsstriker.dto.reports.DashboardDataDTO;
import org.example.tmsstriker.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for generating reports and analytics
 */


@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "report-controller", description = "API for generating reports and analytics")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/test-run/{runId}/summary")
    @Operation(summary = "Get test run execution summary")
    public ResponseEntity<TestRunSummaryDTO> getTestRunSummary(@PathVariable UUID runId) {
        TestRunSummaryDTO summary = reportService.getTestRunSummary(runId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/project/{projectId}/overview")
    @Operation(summary = "Get project overview")
    public ResponseEntity<ProjectOverviewDTO> getProjectOverview(@PathVariable UUID projectId) {
        ProjectOverviewDTO overview = reportService.getProjectOverview(projectId);
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard data")
    public ResponseEntity<DashboardDataDTO> getDashboardData(
            @Parameter(description = "Project ID (optional)")
            @RequestParam(required = false) UUID projectId
    ) {
        DashboardDataDTO dashboard = reportService.getDashboardData(projectId);
        return ResponseEntity.ok(dashboard);
    }
}

