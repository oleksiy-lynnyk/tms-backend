package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tmsstriker.dto.reports.*;
import org.example.tmsstriker.entity.Project;
import org.example.tmsstriker.entity.TestRun;
import org.example.tmsstriker.repository.ProjectRepository;
import org.example.tmsstriker.repository.TestRunRepository;
import org.example.tmsstriker.repository.TestCaseRepository;
import org.example.tmsstriker.repository.TestRunCaseResultRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final TestRunRepository testRunRepository;
    private final ProjectRepository projectRepository;
    private final TestCaseRepository testCaseRepository;
    private final TestRunCaseResultRepository testRunCaseResultRepository;

    public TestRunSummaryDTO getTestRunSummary(UUID testRunId) {
        log.debug("Generating test run summary for testRunId: {}", testRunId);

        TestRun testRun = testRunRepository.findById(testRunId)
                .orElseThrow(() -> new RuntimeException("Test run not found: " + testRunId));

        // TODO: Отримати реальні дані з бази
        Map<String, Integer> statusCounts = getTestRunStatusCounts(testRunId);

        return TestRunSummaryDTO.builder()
                .testRunId(testRunId)
                .testRunName(testRun.getName())
                .status(testRun.getStatus())
                .startedAt(convertToLocalDateTime(testRun.getStartedAt()))
                .completedAt(convertToLocalDateTime(testRun.getCompletedAt()))
                .durationMinutes(calculateDuration(testRun.getStartedAt(), testRun.getCompletedAt()))
                .totalTests(statusCounts.getOrDefault("total", 0))
                .passedTests(statusCounts.getOrDefault("passed", 0))
                .failedTests(statusCounts.getOrDefault("failed", 0))
                .blockedTests(statusCounts.getOrDefault("blocked", 0))
                .skippedTests(statusCounts.getOrDefault("skipped", 0))
                .passRate(calculatePassRate(statusCounts))
                .executionProgress(calculateProgress(statusCounts))
                .assignedToName("John Doe") // TODO: з бази
                .environmentName("QA Environment") // TODO: з бази
                .configurationName("Chrome / Windows") // TODO: з бази
                .versionName("v2.1.0") // TODO: з бази
                .build();
    }

    public ProjectOverviewDTO getProjectOverview(UUID projectId) {
        log.debug("Generating project overview for projectId: {}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found: " + projectId));

        // TODO: Отримати реальні метрики з бази
        Map<String, Integer> testCaseMetrics = getProjectTestCaseMetrics(projectId);
        Map<String, Integer> testRunMetrics = getProjectTestRunMetrics(projectId);

        return ProjectOverviewDTO.builder()
                .projectId(projectId)
                .projectName(project.getName())
                .lastUpdated(LocalDateTime.now())
                .totalTestCases(testCaseMetrics.getOrDefault("total", 0))
                .automatedTests(testCaseMetrics.getOrDefault("automated", 0))
                .manualTests(testCaseMetrics.getOrDefault("manual", 0))
                .automationCoverage(calculateAutomationCoverage(testCaseMetrics))
                .totalTestRuns(testRunMetrics.getOrDefault("total", 0))
                .activeTestRuns(testRunMetrics.getOrDefault("active", 0))
                .completedTestRuns(testRunMetrics.getOrDefault("completed", 0))
                .averagePassRate(85.5) // TODO: розрахувати з бази
                .teamMemberCount(8) // TODO: з бази користувачів
                .executionsThisMonth(156) // TODO: з бази
                .newTestCasesThisWeek(12) // TODO: з бази
                .build();
    }

    public DashboardDataDTO getDashboardData(UUID projectId) {
        log.debug("Generating dashboard data for projectId: {}", projectId);

        return DashboardDataDTO.builder()
                .quickStats(getQuickStats(projectId))
                .build();
    }

    // Helper methods
    private Map<String, Integer> getTestRunStatusCounts(UUID testRunId) {
        // TODO: Реалізувати через repository
        return Map.of(
                "total", 100,
                "passed", 85,
                "failed", 10,
                "blocked", 3,
                "skipped", 2
        );
    }

    private LocalDateTime convertToLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    private Long calculateDuration(Instant startedAt, Instant completedAt) {
        if (startedAt == null || completedAt == null) {
            return null;
        }
        return java.time.Duration.between(startedAt, completedAt).toMinutes();
    }

    private Double calculatePassRate(Map<String, Integer> statusCounts) {
        int total = statusCounts.getOrDefault("total", 0);
        int passed = statusCounts.getOrDefault("passed", 0);
        return total > 0 ? (passed * 100.0) / total : 0.0;
    }

    private Double calculateProgress(Map<String, Integer> statusCounts) {
        int total = statusCounts.getOrDefault("total", 0);
        int executed = statusCounts.getOrDefault("passed", 0) +
                statusCounts.getOrDefault("failed", 0) +
                statusCounts.getOrDefault("blocked", 0);
        return total > 0 ? (executed * 100.0) / total : 0.0;
    }

    private Map<String, Integer> getProjectTestCaseMetrics(UUID projectId) {
        // TODO: Реалізувати через repository
        return Map.of(
                "total", 250,
                "automated", 150,
                "manual", 100
        );
    }

    private Map<String, Integer> getProjectTestRunMetrics(UUID projectId) {
        // TODO: Реалізувати через repository
        return Map.of(
                "total", 25,
                "active", 3,
                "completed", 22
        );
    }

    private Double calculateAutomationCoverage(Map<String, Integer> metrics) {
        int total = metrics.getOrDefault("total", 0);
        int automated = metrics.getOrDefault("automated", 0);
        return total > 0 ? (automated * 100.0) / total : 0.0;
    }

    private Map<String, Object> getQuickStats(UUID projectId) {
        return Map.of(
                "totalProjects", 5,
                "totalTestCases", 1250,
                "totalTestRuns", 125,
                "automationCoverage", 68.5
        );
    }
}