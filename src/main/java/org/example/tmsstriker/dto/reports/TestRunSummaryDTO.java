package org.example.tmsstriker.dto.reports;

import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для підсумку виконання тест-рану
 */
@Data
@Builder
@Schema(description = "Підсумок виконання тест-рану")
public class TestRunSummaryDTO {

    @Schema(description = "ID тест-рану")
    private UUID testRunId;

    @Schema(description = "Назва тест-рану", example = "Sprint 23 Regression")
    private String testRunName;

    @Schema(description = "Поточний статус", example = "Completed")
    private String status;

    @Schema(description = "Час початку виконання")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startedAt;

    @Schema(description = "Час завершення виконання")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;

    @Schema(description = "Загальна тривалість у хвилинах", example = "127")
    private Long durationMinutes;

    // Підрахунки тестів
    @Schema(description = "Загальна кількість тестів", example = "100")
    private Integer totalTests;

    @Schema(description = "Кількість пройдених тестів", example = "85")
    private Integer passedTests;

    @Schema(description = "Кількість неуспішних тестів", example = "10")
    private Integer failedTests;

    @Schema(description = "Кількість заблокованих тестів", example = "3")
    private Integer blockedTests;

    @Schema(description = "Кількість пропущених тестів", example = "2")
    private Integer skippedTests;

    // Відсотки
    @Schema(description = "Відсоток успішних тестів", example = "85.0")
    private Double passRate;

    @Schema(description = "Прогрес виконання у відсотках", example = "100.0")
    private Double executionProgress;

    // Інформація про середовище
    @Schema(description = "Ім'я відповідального", example = "John Doe")
    private String assignedToName;

    @Schema(description = "Тестове середовище", example = "QA Environment")
    private String environmentName;

    @Schema(description = "Конфігурація тестування", example = "Chrome 120 / Windows 11")
    private String configurationName;

    @Schema(description = "Версія продукту", example = "v2.1.0")
    private String versionName;
}