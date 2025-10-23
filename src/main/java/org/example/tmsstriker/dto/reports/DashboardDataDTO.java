package org.example.tmsstriker.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDataDTO {

    // Загальна статистика
    private Long totalProjects;
    private Long totalTestCases;
    private Long totalTestRuns;
    private Long totalTestSuites;

    // Статистика по виконанню
    private Long passedTests;
    private Long failedTests;
    private Long skippedTests;
    private Long blockedTests;

    // Відсоткові показники
    private Double passRate;
    private Double failRate;

    // Останні запуски тестів
    private List<TestRunSummaryDTO> recentTestRuns;

    // Статистика по проектах
    private Map<String, ProjectStatisticsDTO> projectStatistics;

    // Тренди (для графіків)
    private List<TestExecutionTrendDTO> executionTrends;

    // Швидка статистика
    private Map<String, Object> quickStats;

    // Внутрішні DTO класи
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestRunSummaryDTO {
        private String id;
        private String name;
        private String projectName;
        private String status;
        private String startedAt;
        private String completedAt;
        private Long totalTests;
        private Long passedTests;
        private Long failedTests;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectStatisticsDTO {
        private String projectId;
        private String projectName;
        private Long totalTestCases;
        private Long totalTestRuns;
        private Double passRate;
        private String lastRunDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestExecutionTrendDTO {
        private String date;
        private Long passed;
        private Long failed;
        private Long skipped;
        private Long blocked;
        private Double passRate;
    }
}