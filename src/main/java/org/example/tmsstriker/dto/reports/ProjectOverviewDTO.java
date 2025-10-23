package org.example.tmsstriker.dto.reports;

import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO для огляду проекту з ключовими метриками
 */
@Data
@Builder
@Schema(description = "Огляд проекту з ключовими метриками тестування")
public class ProjectOverviewDTO {

    @Schema(description = "ID проекту")
    private UUID projectId;

    @Schema(description = "Назва проекту", example = "E-Commerce Platform")
    private String projectName;

    @Schema(description = "Час останнього оновлення")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;

    // Метрики тест-кейсів
    @Schema(description = "Загальна кількість тест-кейсів", example = "250")
    private Integer totalTestCases;

    @Schema(description = "Кількість автоматизованих тестів", example = "150")
    private Integer automatedTests;

    @Schema(description = "Кількість ручних тестів", example = "100")
    private Integer manualTests;

    @Schema(description = "Відсоток автоматизації", example = "60.0")
    private Double automationCoverage;

    // Метрики тест-ранів
    @Schema(description = "Загальна кількість тест-ранів", example = "25")
    private Integer totalTestRuns;

    @Schema(description = "Кількість активних тест-ранів", example = "3")
    private Integer activeTestRuns;

    @Schema(description = "Кількість завершених тест-ранів", example = "22")
    private Integer completedTestRuns;

    @Schema(description = "Середній відсоток успішних тестів", example = "87.5")
    private Double averagePassRate;

    // Метрики команди
    @Schema(description = "Кількість учасників команди", example = "8")
    private Integer teamMemberCount;

    @Schema(description = "Виконань тестів цього місяця", example = "156")
    private Integer executionsThisMonth;

    @Schema(description = "Нові тест-кейси цього тижня", example = "12")
    private Integer newTestCasesThisWeek;

    // Додаткова інформація
    @Schema(description = "Недавня активність")
    private RecentActivitySummaryDTO recentActivity;

    @Schema(description = "Поточні сповіщення та проблеми")
    private List<ProjectAlertDTO> alerts;

    @Schema(description = "Ключові показники ефективності")
    private ProjectKPIsDTO kpis;

    @Schema(description = "Розподіл тестів по компонентах")
    private List<ComponentStatsDTO> componentStats;
}

/**
 * Підсумок недавньої активності
 */
@Data
@Builder
@Schema(description = "Підсумок недавньої активності в проекті")
class RecentActivitySummaryDTO {

    @Schema(description = "Активність за останні 24 години")
    private Integer activitiesLast24h;

    @Schema(description = "Найактивніший користувач", example = "john.doe")
    private String mostActiveUser;

    @Schema(description = "Останній запущений тест-ран")
    private String lastTestRunName;

    @Schema(description = "Час останньої активності")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastActivityAt;
}

/**
 * Сповіщення проекту
 */
@Data
@Builder
@Schema(description = "Сповіщення або попередження по проекту")
class ProjectAlertDTO {

    @Schema(description = "Тип сповіщення", allowableValues = {"info", "warning", "error", "success"})
    private String type;

    @Schema(description = "Заголовок сповіщення", example = "Low automation coverage")
    private String title;

    @Schema(description = "Повідомлення", example = "Automation coverage is below 50% threshold")
    private String message;

    @Schema(description = "Рівень важливості", allowableValues = {"Low", "Medium", "High", "Critical"})
    private String severity;

    @Schema(description = "Час створення сповіщення")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "URL для дії")
    private String actionUrl;

    @Schema(description = "Чи підтверджено сповіщення")
    private Boolean acknowledged;
}

/**
 * Ключові показники ефективності проекту
 */
@Data
@Builder
@Schema(description = "Ключові показники ефективності (KPI) проекту")
class ProjectKPIsDTO {

    @Schema(description = "Швидкість тестування (тестів на день)", example = "23.5")
    private Double testVelocity;

    @Schema(description = "Рівень виявлення дефектів", example = "12.3")
    private Double defectDetectionRate;

    @Schema(description = "Накладні витрати на підтримку тестів (%)", example = "15.2")
    private Double maintenanceOverhead;

    @Schema(description = "Оцінка впливу на час виходу на ринок", example = "8.7")
    private Double timeToMarketScore;

    @Schema(description = "Дотримання критеріїв якості (%)", example = "94.5")
    private Double qualityGateCompliance;

    @Schema(description = "Середній час виконання тесту (секунди)", example = "45.8")
    private Double avgExecutionTime;
}

/**
 * Статистика по компонентах
 */
@Data
@Builder
@Schema(description = "Статистика тестування по компонентах")
class ComponentStatsDTO {

    @Schema(description = "Назва компонента", example = "User Authentication")
    private String componentName;

    @Schema(description = "Кількість тестів", example = "45")
    private Integer testCount;

    @Schema(description = "Кількість автоматизованих тестів", example = "32")
    private Integer automatedCount;

    @Schema(description = "Відсоток автоматизації", example = "71.1")
    private Double automationPercentage;

    @Schema(description = "Останній відсоток успішних тестів", example = "89.5")
    private Double lastPassRate;

    @Schema(description = "Рівень ризику", allowableValues = {"Low", "Medium", "High", "Critical"})
    private String riskLevel;
}
