package org.example.tmsstriker.dto;

import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO для доступних значень фільтрів
 */
@Data
@Builder
@Schema(description = "Доступні значення для фільтрів пошуку тест-кейсів")
public class TestCaseFiltersDTO {

    @Schema(description = "Доступні пріоритети")
    private List<String> priorities;

    @Schema(description = "Доступні статуси")
    private List<String> statuses;

    @Schema(description = "Доступні статуси автоматизації")
    private List<String> automationStatuses;

    @Schema(description = "Доступні власники")
    private List<String> owners;

    @Schema(description = "Доступні теги")
    private List<String> tags;

    @Schema(description = "Доступні компоненти")
    private List<String> components;

    @Schema(description = "Доступні типи")
    private List<String> types;

    @Schema(description = "Статистика тест-кейсів")
    private TestCaseStatsDTO stats;
}

